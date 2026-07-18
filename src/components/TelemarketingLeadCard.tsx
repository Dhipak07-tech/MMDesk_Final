import React, { useState } from 'react';
import { Phone, User, Calendar, MapPin, Megaphone, Check, Trash2, Mail, ExternalLink } from 'lucide-react';

interface Lead {
  id: string;
  name: string;
  phone: string;
  email: string;
  location: string;
  campaign: string;
  status: 'New' | 'Interested' | 'Not Interested' | 'Callback';
  scheduledTime?: string;
  notes: string;
}

interface TelemarketingLeadCardProps {
  lead?: Lead;
  onCallInitiated?: (lead: Lead) => void;
  onStatusChange?: (leadId: string, newStatus: Lead['status']) => void;
  onDelete?: (leadId: string) => void;
}

export const TelemarketingLeadCard: React.FC<TelemarketingLeadCardProps> = ({
  lead = {
    id: "lead_987214",
    name: "Eleanor Vance",
    phone: "+1 (555) 234-5678",
    email: "e.vance@enterprise.io",
    location: "Chicago, IL",
    campaign: "Q3 Premium Cloud Migration",
    status: "Interested",
    scheduledTime: "Today at 3:30 PM",
    notes: "Expressed interest in hybrid cloud hosting solutions. Requested migration roadmap and preliminary pricing structure.",
  },
  onCallInitiated,
  onStatusChange,
  onDelete,
}) => {
  const [currentStatus, setCurrentStatus] = useState<Lead['status']>(lead.status);
  const [isCalling, setIsCalling] = useState(false);

  const handleStatusChange = (status: Lead['status']) => {
    setCurrentStatus(status);
    if (onStatusChange) onStatusChange(lead.id, status);
  };

  const startCall = () => {
    setIsCalling(true);
    if (onCallInitiated) onCallInitiated(lead);
    setTimeout(() => {
      setIsCalling(false);
      handleStatusChange('Callback');
    }, 3000);
  };

  // Status Style Maps using Success (#10B981), Danger (#EF4444), and Primary (#586EF0) colors
  const statusStyles = {
    New: 'bg-[#586EF0]/15 text-[#586EF0] border-[#586EF0]/20',
    Interested: 'bg-[#10B981]/15 text-[#10B981] border-[#10B981]/20',
    'Not Interested': 'bg-[#EF4444]/15 text-[#EF4444] border-[#EF4444]/20',
    Callback: 'bg-amber-500/15 text-amber-600 border-amber-500/20'
  };

  return (
    <div className="bg-white border border-[#E5E7EB] rounded-[14px] shadow-[0_2px_10px_rgba(15,23,42,0.08)] p-5 max-w-md w-full transition-all duration-200 hover:-translate-y-0.5 relative overflow-hidden text-left flex flex-col justify-between h-[320px]">
      
      {/* Background Accent glow */}
      <div className="absolute top-0 right-0 w-24 h-24 bg-[#586EF0]/5 rounded-full blur-2xl pointer-events-none" />

      {/* Header Section */}
      <div>
        <div className="flex items-center justify-between mb-3.5">
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-[10px] bg-[#586EF0]/10 flex items-center justify-center text-[#586EF0]">
              <Megaphone className="w-3.5 h-3.5" />
            </div>
            <div>
              {/* Subtitle - uppercase, tracking 0.5px, 12px, semi-bold */}
              <h3 className="text-[12px] font-semibold text-[#374151] tracking-[0.5px] uppercase truncate max-w-[180px]">
                {lead.campaign}
              </h3>
            </div>
          </div>

          {/* Status Badge */}
          <span className={`inline-flex items-center px-2 py-0.5 rounded-[10px] text-[8px] font-bold uppercase tracking-wider border ${statusStyles[currentStatus]}`}>
            <span className="w-1 h-1 rounded-full bg-current mr-1 animate-pulse" />
            {currentStatus}
          </span>
        </div>

        {/* Lead Name - H2 (17px, Bold, #111827) */}
        <div className="flex items-start justify-between">
          <div className="space-y-1">
            <h2 className="text-[17px] font-bold text-[#111827] leading-tight flex items-center gap-1.5">
              <User className="w-4 h-4 text-[#6B7280]" />
              {lead.name}
            </h2>
            <div className="flex items-center gap-3 text-[11px] text-[#6B7280]">
              <span className="flex items-center gap-1">
                <MapPin className="w-3 h-3 text-[#9CA3AF]" />
                {lead.location}
              </span>
              <span className="flex items-center gap-1 flex-1 truncate">
                <Mail className="w-3 h-3 text-[#9CA3AF] shrink-0" />
                <span className="truncate max-w-[120px] inline-block">{lead.email}</span>
              </span>
            </div>
          </div>
        </div>

        {/* Body Description/Notes - 11px, #6B7280, leading 1.35 */}
        <p className="text-[11px] text-[#6B7280] leading-[1.35] mt-3.5 bg-[#F4F7FA] p-2.5 rounded-[10px] border border-slate-100">
          <span className="font-bold text-[#374151] block mb-1">Interaction Notes:</span>
          {lead.notes}
        </p>
      </div>

      {/* Call Details & Footer */}
      <div className="mt-4 space-y-3">
        {lead.scheduledTime && (
          <div className="flex items-center gap-1.5 text-[11px] text-[#6B7280] bg-amber-50/50 dark:bg-amber-950/15 border border-amber-100 dark:border-amber-900/20 px-2.5 py-1.5 rounded-[10px]">
            <Calendar className="w-3.5 h-3.5 text-amber-500 shrink-0" />
            <span>Scheduled Callback: <strong className="text-amber-700 dark:text-amber-400">{lead.scheduledTime}</strong></span>
          </div>
        )}

        {/* Action Bar */}
        <div className="flex items-center justify-between pt-3 border-t border-[#D9DBE1]">
          {/* Quick status selector */}
          <div className="flex items-center gap-1">
            <button
              onClick={() => handleStatusChange('Interested')}
              title="Mark Interested"
              className={`p-1.5 rounded-[10px] border transition-colors cursor-pointer ${
                currentStatus === 'Interested'
                  ? 'bg-[#10B981] border-[#10B981] text-white'
                  : 'bg-white border-[#E5E7EB] hover:bg-slate-50 text-[#6B7280]'
              }`}
            >
              <Check className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={() => handleStatusChange('Not Interested')}
              title="Mark Not Interested"
              className={`p-1.5 rounded-[10px] border transition-colors cursor-pointer ${
                currentStatus === 'Not Interested'
                  ? 'bg-[#EF4444] border-[#EF4444] text-white'
                  : 'bg-white border-[#E5E7EB] hover:bg-slate-50 text-[#6B7280]'
              }`}
            >
              <Trash2 className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* Action Button - Primary theme color, rounded 10px */}
          <button
            onClick={startCall}
            disabled={isCalling}
            className={`px-4 py-2 rounded-[10px] font-bold text-[11px] text-white shadow-sm flex items-center gap-1.5 transition-all cursor-pointer ${
              isCalling
                ? 'bg-amber-500 hover:bg-amber-600'
                : 'bg-[#586EF0] hover:bg-[#7084F4]'
            }`}
          >
            <Phone className={`w-3.5 h-3.5 ${isCalling ? 'animate-bounce' : ''}`} />
            {isCalling ? 'Dialing...' : 'Initiate Call'}
          </button>
        </div>
      </div>
    </div>
  );
};
