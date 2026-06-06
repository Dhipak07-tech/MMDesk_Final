/**
 * ═══════════════════════════════════════════════════════════════════════════
 * Microsoft Graph API Email Engine — OAuth2 Modern Authentication
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Replaces Basic Auth SMTP/IMAP with Microsoft Graph API (OAuth2).
 * Required because the tenant has Security Defaults enforcing Modern Auth.
 *
 * Send mail  : POST /v1.0/users/{email}/sendMail
 * Read mail  : GET  /v1.0/users/{email}/mailFolders/Inbox/messages
 *
 * App Registration setup (one-time, ~5 minutes):
 *   1. https://portal.azure.com → Azure AD → App Registrations → New
 *   2. Name: "Ticklora Email" | Supported account types: Single tenant
 *   3. Certificates & Secrets → New client secret → copy value
 *   4. API Permissions → Add → Microsoft Graph → Application permissions:
 *        Mail.Send        (send emails as the mailbox)
 *        Mail.ReadWrite   (read/mark emails in the inbox)
 *      → Grant admin consent
 *   5. Copy Tenant ID + Client ID from the Overview page
 *   6. Paste into .env: GRAPH_TENANT_ID, GRAPH_CLIENT_ID, GRAPH_CLIENT_SECRET
 *
 * ADDITIVE — does not modify any existing engine.
 */

import { execute, query, formatDate } from './db';

// ─── Config ───────────────────────────────────────────────────────────────────
export const GRAPH_CONFIG = {
  TENANT_ID:    process.env.GRAPH_TENANT_ID    || '',
  CLIENT_ID:    process.env.GRAPH_CLIENT_ID    || '',
  CLIENT_SECRET: process.env.GRAPH_CLIENT_SECRET || '',
  USER_EMAIL:   process.env.GRAPH_USER_EMAIL   || 'support@technosprint.net',
};

const GRAPH_BASE = 'https://graph.microsoft.com/v1.0';
const TOKEN_URL  = `https://login.microsoftonline.com/${GRAPH_CONFIG.TENANT_ID}/oauth2/v2.0/token`;

// ─── Token cache (in-memory, refreshes before expiry) ────────────────────────
let _tokenCache: { token: string; expiresAt: number } | null = null;

export async function getGraphToken(): Promise<string> {
  const now = Date.now();
  // Reuse cached token if still valid (with 2 min buffer)
  if (_tokenCache && _tokenCache.expiresAt > now + 120_000) {
    return _tokenCache.token;
  }

  const { TENANT_ID, CLIENT_ID, CLIENT_SECRET } = GRAPH_CONFIG;

  if (!TENANT_ID || !CLIENT_ID || !CLIENT_SECRET ||
      TENANT_ID === 'your_tenant_id_here') {
    throw new Error(
      'Microsoft Graph credentials not configured. ' +
      'Set GRAPH_TENANT_ID, GRAPH_CLIENT_ID, GRAPH_CLIENT_SECRET in .env. ' +
      'See setup instructions: https://portal.azure.com → App Registrations'
    );
  }

  const params = new URLSearchParams({
    grant_type:    'client_credentials',
    client_id:     CLIENT_ID,
    client_secret: CLIENT_SECRET,
    scope:         'https://graph.microsoft.com/.default',
  });

  const res = await fetch(TOKEN_URL, {
    method:  'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body:    params.toString(),
  });

  if (!res.ok) {
    const err = await res.text();
    throw new Error(`Graph token request failed (${res.status}): ${err}`);
  }

  const data = await res.json() as any;
  _tokenCache = {
    token:     data.access_token,
    expiresAt: now + (data.expires_in - 60) * 1000,
  };
  return _tokenCache.token;
}

// ─── Check if Graph API is configured ────────────────────────────────────────
export function isGraphConfigured(): boolean {
  const { TENANT_ID, CLIENT_ID, CLIENT_SECRET } = GRAPH_CONFIG;
  return !!(
    TENANT_ID && TENANT_ID !== 'your_tenant_id_here' &&
    CLIENT_ID && CLIENT_ID !== 'your_client_id_here' &&
    CLIENT_SECRET && CLIENT_SECRET !== 'your_client_secret_here'
  );
}

// ─── Send email via Graph API ─────────────────────────────────────────────────
export async function sendViaGraph(params: {
  to:            string | string[];
  subject:       string;
  html:          string;
  ticketNumber?: string;
  replyToMsgId?: string;
  attachments?:  Array<{ name: string; contentType: string; contentBytes: string }>;
}): Promise<{ ok: boolean; messageId?: string; error?: string }> {
  const { to, subject, html, ticketNumber, replyToMsgId, attachments } = params;
  const recipients = Array.isArray(to) ? to : [to];

  try {
    const token    = await getGraphToken();
    const userMail = GRAPH_CONFIG.USER_EMAIL;

    const toAddresses = recipients
      .filter(e => e && e.includes('@'))
      .map(e => ({ emailAddress: { address: e.trim() } }));

    if (toAddresses.length === 0) {
      return { ok: false, error: 'No valid recipient email addresses' };
    }

    const message: any = {
      subject,
      importance: 'normal',
      body:        { contentType: 'HTML', content: html },
      toRecipients: toAddresses,
      from:        { emailAddress: { address: userMail, name: 'Technosprint Support' } },
      replyTo:     [{ emailAddress: { address: userMail, name: 'Technosprint Support' } }],
    };

    if (ticketNumber) {
      message.internetMessageHeaders = [
        { name: 'X-Ticket-Number', value: ticketNumber },
      ];
    }
    if (replyToMsgId) {
      message.conversationId = replyToMsgId;
    }
    if (attachments && attachments.length > 0) {
      message.attachments = attachments.map(a => ({
        '@odata.type':      '#microsoft.graph.fileAttachment',
        name:               a.name,
        contentType:        a.contentType,
        contentBytes:       a.contentBytes,
      }));
    }

    const endpoint = `${GRAPH_BASE}/users/${encodeURIComponent(userMail)}/sendMail`;
    const res = await fetch(endpoint, {
      method:  'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ message, saveToSentItems: true }),
    });

    if (!res.ok) {
      const err = await res.text();
      throw new Error(`Graph sendMail failed (${res.status}): ${err}`);
    }

    // Graph sendMail returns 202 with no body — use a synthetic message ID
    const msgId = `<graph-${Date.now()}@technosprint.net>`;
    await logGraphEvent({
      event_type: 'email_sent', direction: 'outbound', status: 'sent',
      ticket_number: ticketNumber, sender: userMail,
      recipient: recipients.join(', '), subject, message_id: msgId,
    });
    console.log(`[Graph] ✓ Email sent → ${recipients.join(', ')} (${subject})`);
    return { ok: true, messageId: msgId };
  } catch (e: any) {
    await logGraphEvent({
      event_type: 'email_sent', direction: 'outbound', status: 'failed',
      ticket_number: ticketNumber, sender: GRAPH_CONFIG.USER_EMAIL,
      recipient: recipients.join(', '), subject, error_msg: e.message,
    });
    console.error('[Graph] Send failed:', e.message);
    return { ok: false, error: e.message };
  }
}

// ─── Read inbox via Graph API (replaces IMAP polling) ────────────────────────
export async function pollInboxViaGraph(
  processEmail: (msg: GraphMessage) => Promise<void>
): Promise<{ polled: number; errors: number }> {
  let polled = 0, errors = 0;
  try {
    const token    = await getGraphToken();
    const userMail = GRAPH_CONFIG.USER_EMAIL;

    // Fetch unread messages from Inbox
    const endpoint = `${GRAPH_BASE}/users/${encodeURIComponent(userMail)}/mailFolders/Inbox/messages` +
      `?$filter=isRead eq false` +
      `&$select=id,subject,from,toRecipients,ccRecipients,body,receivedDateTime,internetMessageId,conversationId,hasAttachments` +
      `&$top=25` +
      `&$orderby=receivedDateTime asc`;

    const res = await fetch(endpoint, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
      const err = await res.text();
      throw new Error(`Graph inbox read failed (${res.status}): ${err}`);
    }

    const data = await res.json() as any;
    const messages: any[] = data.value || [];

    if (messages.length > 0) {
      console.log(`[Graph] Found ${messages.length} unread messages in inbox`);
    }

    for (const msg of messages) {
      try {
        // Check for duplicate processing
        const existing = await query(
          'SELECT id FROM email_logs WHERE message_id = ?',
          [msg.internetMessageId]
        );
        if (existing.length > 0) {
          // Already processed — just mark as read
          await markMessageRead(token, userMail, msg.id);
          continue;
        }

        // Fetch full message with attachments if needed
        let fullMsg = msg;
        if (msg.hasAttachments) {
          const attRes = await fetch(
            `${GRAPH_BASE}/users/${encodeURIComponent(userMail)}/messages/${msg.id}?$expand=attachments`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          if (attRes.ok) fullMsg = await attRes.json();
        }

        const graphMsg: GraphMessage = {
          id:              msg.id,
          messageId:       msg.internetMessageId || `<${msg.id}@graph>`,
          subject:         msg.subject || '(No Subject)',
          from:            msg.from?.emailAddress?.address || 'unknown',
          fromName:        msg.from?.emailAddress?.name || '',
          to:              (msg.toRecipients || []).map((r: any) => r.emailAddress?.address).filter(Boolean),
          cc:              (msg.ccRecipients  || []).map((r: any) => r.emailAddress?.address).filter(Boolean),
          bodyText:        msg.body?.content || '',
          bodyType:        msg.body?.contentType || 'text',
          receivedAt:      msg.receivedDateTime,
          conversationId:  msg.conversationId,
          attachments:     (fullMsg.attachments || []).map((a: any) => ({
            name:         a.name,
            contentType:  a.contentType,
            size:         a.size,
            contentBytes: a.contentBytes,
          })),
        };

        await processEmail(graphMsg);

        // Mark as read after processing
        await markMessageRead(token, userMail, msg.id);
        polled++;

        await logGraphEvent({
          event_type: 'email_received', direction: 'inbound', status: 'success',
          sender: graphMsg.from, subject: graphMsg.subject,
          message_id: graphMsg.messageId,
        });
      } catch (msgErr: any) {
        errors++;
        console.error('[Graph] Error processing message:', msgErr.message);
        await logGraphEvent({
          event_type: 'email_received', direction: 'inbound', status: 'failed',
          sender: msg.from?.emailAddress?.address, subject: msg.subject,
          error_msg: msgErr.message,
        });
      }
    }
  } catch (e: any) {
    errors++;
    console.error('[Graph] pollInbox error:', e.message);
    await logGraphEvent({
      event_type: 'imap_poll', direction: 'inbound', status: 'failed',
      error_msg: e.message,
    });
  }
  return { polled, errors };
}

// ─── Mark message as read ─────────────────────────────────────────────────────
async function markMessageRead(token: string, userEmail: string, messageId: string) {
  try {
    await fetch(
      `${GRAPH_BASE}/users/${encodeURIComponent(userEmail)}/messages/${messageId}`,
      {
        method:  'PATCH',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body:    JSON.stringify({ isRead: true }),
      }
    );
  } catch { /* non-critical */ }
}

// ─── Test Graph connectivity ──────────────────────────────────────────────────
export async function testGraphConnection(): Promise<{ ok: boolean; msg: string; detail?: string }> {
  try {
    if (!isGraphConfigured()) {
      return {
        ok: false,
        msg: 'Graph API not configured',
        detail: 'Set GRAPH_TENANT_ID, GRAPH_CLIENT_ID, GRAPH_CLIENT_SECRET in .env',
      };
    }
    const token = await getGraphToken();
    // Verify the token works by reading mailbox info
    const res = await fetch(
      `${GRAPH_BASE}/users/${encodeURIComponent(GRAPH_CONFIG.USER_EMAIL)}/mailboxSettings`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    if (!res.ok) {
      const err = await res.text();
      throw new Error(`Mailbox access failed (${res.status}): ${err}`);
    }
    await logGraphEvent({ event_type: 'graph_test', direction: 'outbound', status: 'success', sender: GRAPH_CONFIG.USER_EMAIL });
    return { ok: true, msg: `Graph API connected — mailbox: ${GRAPH_CONFIG.USER_EMAIL}` };
  } catch (e: any) {
    await logGraphEvent({ event_type: 'graph_test', direction: 'outbound', status: 'failed', error_msg: e.message });
    return { ok: false, msg: e.message };
  }
}

// ─── Send test email via Graph ────────────────────────────────────────────────
export async function sendGraphTestEmail(to?: string): Promise<{ ok: boolean; messageId?: string; error?: string }> {
  const recipient = to || GRAPH_CONFIG.USER_EMAIL;
  return sendViaGraph({
    to:      recipient,
    subject: `[TEST] Microsoft Graph Email — ${new Date().toLocaleString()}`,
    html: `<div style="font-family:sans-serif;padding:24px;max-width:600px;margin:0 auto">
      <h2 style="color:#0078d4">✅ Microsoft Graph Email Working</h2>
      <p>This confirms <strong>Microsoft Graph API</strong> with OAuth2 is operational.</p>
      <table style="border-collapse:collapse;font-size:13px;margin-top:16px;width:100%">
        <tr><td style="padding:6px 12px;color:#555;font-weight:600">Auth Method</td><td style="padding:6px 12px">OAuth2 Client Credentials</td></tr>
        <tr><td style="padding:6px 12px;color:#555;font-weight:600">API Endpoint</td><td style="padding:6px 12px">graph.microsoft.com/v1.0</td></tr>
        <tr><td style="padding:6px 12px;color:#555;font-weight:600">Sender</td><td style="padding:6px 12px">${GRAPH_CONFIG.USER_EMAIL}</td></tr>
        <tr><td style="padding:6px 12px;color:#555;font-weight:600">Recipient</td><td style="padding:6px 12px">${recipient}</td></tr>
        <tr><td style="padding:6px 12px;color:#555;font-weight:600">Sent at</td><td style="padding:6px 12px">${new Date().toISOString()}</td></tr>
      </table>
      <p style="margin-top:20px;font-size:12px;color:#888">Ticklora ITSM — auto-generated test</p>
    </div>`,
  });
}

// ─── Audit logger ─────────────────────────────────────────────────────────────
async function logGraphEvent(data: {
  event_type:    string;
  direction?:    string;
  status:        string;
  ticket_number?: string;
  sender?:       string;
  recipient?:    string;
  subject?:      string;
  message_id?:   string;
  error_msg?:    string;
}) {
  try {
    await execute(
      `INSERT INTO m365_email_audit
        (event_type, direction, status, ticket_number, sender, recipient, subject, message_id, error_msg)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        data.event_type, data.direction ?? 'outbound', data.status,
        data.ticket_number ?? null, data.sender ?? null, data.recipient ?? null,
        data.subject ?? null, data.message_id ?? null, data.error_msg ?? null,
      ]
    );
  } catch { /* non-critical */ }
}

// ─── Types ────────────────────────────────────────────────────────────────────
export interface GraphMessage {
  id:            string;
  messageId:     string;
  subject:       string;
  from:          string;
  fromName:      string;
  to:            string[];
  cc:            string[];
  bodyText:      string;
  bodyType:      string;
  receivedAt:    string;
  conversationId?: string;
  attachments:   Array<{
    name:         string;
    contentType:  string;
    size:         number;
    contentBytes?: string;
  }>;
}

// ─── Queue-based delivery via Graph (called by processEmailQueue) ─────────────
export async function processQueuedEmailViaGraph(job: any): Promise<boolean> {
  if (!isGraphConfigured()) return false;
  const result = await sendViaGraph({
    to:            job.recipient,
    subject:       job.subject,
    html:          job.body_html,
    ticketNumber:  job.ticket_number,
  });
  return result.ok;
}

// ─── Health check ─────────────────────────────────────────────────────────────
export async function getGraphHealth(): Promise<{
  configured: boolean;
  connected:  boolean | null;
  error?:     string;
  userEmail:  string;
  tenantId:   string;
}> {
  const configured = isGraphConfigured();
  if (!configured) {
    return {
      configured: false,
      connected:  null,
      error:      'GRAPH_TENANT_ID / GRAPH_CLIENT_ID / GRAPH_CLIENT_SECRET not set in .env',
      userEmail:  GRAPH_CONFIG.USER_EMAIL,
      tenantId:   GRAPH_CONFIG.TENANT_ID || 'not set',
    };
  }
  const result = await testGraphConnection();
  return {
    configured: true,
    connected:  result.ok,
    error:      result.ok ? undefined : result.msg,
    userEmail:  GRAPH_CONFIG.USER_EMAIL,
    tenantId:   GRAPH_CONFIG.TENANT_ID,
  };
}
