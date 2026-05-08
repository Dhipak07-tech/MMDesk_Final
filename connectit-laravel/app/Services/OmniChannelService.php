<?php

namespace App\Services;

use App\Models\NotificationQueue;
use App\Models\Ticket;
use App\Models\TicketActivity;
use App\Enums\NotificationChannel;
use App\Enums\QueueStatus;
use App\Enums\ActivityType;
use App\Enums\VisibilityType;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Log;
use Twilio\Rest\Client as TwilioClient;
use Exception;

class OmniChannelService
{
    protected ?TwilioClient $twilio = null;

    public function __construct()
    {
        $sid = config('services.twilio.sid');
        $token = config('services.twilio.token');
        
        if ($sid && $token) {
            // Workaround for local Windows SSL certificate issues
            $options = [
                'httpClient' => new \Twilio\Http\GuzzleClient(new \GuzzleHttp\Client(['verify' => false]))
            ];
            $this->twilio = new TwilioClient($sid, $token, null, null, $options['httpClient']);
        }
    }

    /**
     * Send an email notification
     */
    public function sendEmail(Ticket $ticket, string $recipient, string $subject, string $body, array $metadata = []): bool
    {
        $log = NotificationQueue::create([
            'ticket_id' => $ticket->id,
            'recipient' => $recipient,
            'channel' => NotificationChannel::EmailChannel->value,
            'subject' => $subject,
            'body' => $body,
            'status' => QueueStatus::Pending,
        ]);

        try {
            Mail::to($recipient)->send(new \App\Mail\OmnichannelMail(
                $subject,
                'emails.ticket_notification',
                [
                    'ticket' => $ticket,
                    'message_body' => $body,
                ]
            ));

            $log->update([
                'status' => QueueStatus::Sent,
                'sent_at' => now(),
            ]);

            $this->logActivity($ticket, "Email sent to $recipient: $subject", ActivityType::EmailSent, VisibilityType::Public, [
                'recipient' => $recipient,
                'subject' => $subject,
                'channel' => 'email'
            ]);

            return true;
        } catch (Exception $e) {
            Log::error("Failed to send email to $recipient: " . $e->getMessage());
            $log->update([
                'status' => QueueStatus::Failed,
                'last_error' => $e->getMessage(),
            ]);
            return false;
        }
    }

    /**
     * Send a WhatsApp notification
     */
    public function sendWhatsApp(Ticket $ticket, string $phone, string $message): bool
    {
        $formattedPhone = $this->normalizePhoneNumber($phone);
        
        $log = NotificationQueue::create([
            'ticket_id' => $ticket->id,
            'recipient' => $formattedPhone,
            'channel' => NotificationChannel::WhatsAppChannel->value,
            'body' => $message,
            'status' => QueueStatus::Pending,
        ]);

        if (!$this->twilio) {
            $error = "Twilio credentials not configured";
            Log::error($error);
            $log->update(['status' => QueueStatus::Failed, 'last_error' => $error]);
            return false;
        }

        try {
            $from = config('services.twilio.from');
            $response = $this->twilio->messages->create(
                "whatsapp:$formattedPhone",
                [
                    "from" => $from,
                    "body" => $message
                ]
            );

            $log->update([
                'status' => QueueStatus::Sent,
                'sent_at' => now(),
            ]);

            $this->logActivity($ticket, "WhatsApp message sent to $formattedPhone", ActivityType::WhatsAppSent, VisibilityType::Public, [
                'recipient' => $formattedPhone,
                'sid' => $response->sid,
                'channel' => 'whatsapp'
            ]);

            return true;
        } catch (Exception $e) {
            Log::error("Failed to send WhatsApp to $formattedPhone: " . $e->getMessage());
            $log->update([
                'status' => QueueStatus::Failed,
                'last_error' => $e->getMessage(),
            ]);
            return false;
        }
    }

    /**
     * Send notifications for a newly created ticket
     */
    public function notifyTicketCreated(Ticket $ticket): void
    {
        $contacts = $this->resolveContacts($ticket);
        $subject = "New Ticket Created: [{$ticket->ticket_number}] {$ticket->title}";
        $body = "A new ticket has been created for you. Ticket number: {$ticket->ticket_number}. We will update you shortly.";

        if ($contacts['email']) {
            \App\Jobs\SendEmailNotification::dispatch($ticket, $contacts['email'], $subject, $body);
        }

        if ($contacts['phone']) {
            \App\Jobs\SendWhatsAppNotification::dispatch($ticket, $contacts['phone'], $body);
        }
    }

    /**
     * Send notifications for ticket assignment
     */
    public function notifyTicketAssigned(Ticket $ticket): void
    {
        if (!$ticket->assigned_to_name) return;

        $contacts = $this->resolveContacts($ticket);
        $subject = "Ticket Assigned: [{$ticket->ticket_number}] {$ticket->title}";
        $body = "Your ticket [{$ticket->ticket_number}] has been assigned to {$ticket->assigned_to_name}.";

        if ($contacts['email']) {
            \App\Jobs\SendEmailNotification::dispatch($ticket, $contacts['email'], $subject, $body);
        }

        if ($contacts['phone']) {
            \App\Jobs\SendWhatsAppNotification::dispatch($ticket, $contacts['phone'], $body);
        }
    }

    /**
     * Send notifications for public comments
     */
    public function notifyCommentAdded(Ticket $ticket, string $comment): void
    {
        $contacts = $this->resolveContacts($ticket);
        $subject = "New Comment on [{$ticket->ticket_number}]";
        $body = "A new update has been added to your ticket:\n\n$comment";

        if ($contacts['email']) {
            \App\Jobs\SendEmailNotification::dispatch($ticket, $contacts['email'], $subject, $body);
        }

        if ($contacts['phone']) {
            \App\Jobs\SendWhatsAppNotification::dispatch($ticket, $contacts['phone'], $body);
        }
    }

    /**
     * Send notifications for ticket resolution
     */
    public function notifyTicketResolved(Ticket $ticket): void
    {
        $contacts = $this->resolveContacts($ticket);
        $subject = "Ticket Resolved: [{$ticket->ticket_number}]";
        $body = "Your ticket [{$ticket->ticket_number}] has been resolved. If you're not satisfied, you can reply to this message.";

        if ($contacts['email']) {
            \App\Jobs\SendEmailNotification::dispatch($ticket, $contacts['email'], $subject, $body);
        }

        if ($contacts['phone']) {
            \App\Jobs\SendWhatsAppNotification::dispatch($ticket, $contacts['phone'], $body);
        }
    }

    /**
     * Resolve email and phone from ticket or caller profile
     */
    protected function resolveContacts(Ticket $ticket): array
    {
        $email = $ticket->caller_email;
        $phone = null;

        // Try to resolve from caller_user_id
        if ($ticket->caller_user_id) {
            $user = \App\Models\User::where('uid', $ticket->caller_user_id)->first();
            if ($user) {
                $email = $email ?: $user->email;
                $phone = $user->phone;
            }
        }

        // Fallback: Check if caller field itself is an email or phone
        if (!$email && filter_var($ticket->caller, FILTER_VALIDATE_EMAIL)) {
            $email = $ticket->caller;
        }

        if (!$phone && preg_match('/^\+?[0-9]{10,15}$/', $ticket->caller)) {
            $phone = $ticket->caller;
        }

        return [
            'email' => $email,
            'phone' => $phone
        ];
    }

    /**
     * Normalize phone numbers to E.164 (specifically handles Indian numbers)
     */
    public function normalizePhoneNumber(string $phone): string
    {
        $phone = preg_replace('/[^0-9]/', '', $phone);
        
        // Handle Indian numbers starting with 0
        if (strlen($phone) === 11 && str_starts_with($phone, '0')) {
            $phone = '91' . substr($phone, 1);
        }
        
        // Handle Indian numbers without country code (10 digits)
        if (strlen($phone) === 10) {
            $phone = '91' . $phone;
        }
        
        // Prepend + if not present
        return '+' . $phone;
    }

    /**
     * Log activity to the ticket timeline
     */
    protected function logActivity(Ticket $ticket, string $message, ActivityType $type, VisibilityType $visibility, array $metadata = []): void
    {
        TicketActivity::create([
            'ticket_id' => $ticket->id,
            'activity_type' => $type->value,
            'visibility_type' => $visibility->value,
            'channel' => 'system',
            'message' => $message,
            'metadata_json' => $metadata,
            'created_by' => 'system',
            'created_by_name' => 'Omnichannel Service',
        ]);
    }
}
