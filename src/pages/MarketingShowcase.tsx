import React, { useState } from 'react';
import { TelemarketingLeadCard } from '../components/TelemarketingLeadCard';
import { CampaignAnalyticsWidget } from '../components/CampaignAnalyticsWidget';
import { Palette, Layers, Award, Sparkles, Megaphone, PhoneCall, Check, ExternalLink, AlertTriangle } from 'lucide-react';

export function MarketingShowcase() {
  const [activeCallLead, setActiveCallLead] = useState<string | null>(null);
  const [totalCalls, setTotalCalls] = useState(1248);
  const [conversions, setConversions] = useState(342);

  const mockLeads = [
    {
      id: "lead_001",
      name: "Eleanor Vance",
      phone: "+1 (555) 234-5678",
      email: "e.vance@enterprise.io",
      location: "Chicago, IL",
      campaign: "Q3 Premium Cloud Migration",
      status: "Interested" as const,
      scheduledTime: "Today at 3:30 PM",
      notes: "Expressed interest in hybrid cloud hosting solutions. Requested migration roadmap and preliminary pricing structure.",
    },
    {
      id: "lead_002",
      name: "Arthur Pendelton",
      phone: "+1 (555) 876-5432",
      email: "a.pendelton@globex.org",
      location: "Austin, TX",
      campaign: "Q3 Premium Cloud Migration",
      status: "New" as const,
      notes: "Inbound registration request received. Senior stakeholder interested in enterprise subscription SLA options.",
    },
    {
      id: "lead_003",
      name: "Sophia Martinez",
      phone: "+1 (555) 432-1098",
      email: "s.martinez@vertex.com",
      location: "Denver, CO",
      campaign: "Q3 Premium Cloud Migration",
      status: "Callback" as const,
      scheduledTime: "Tomorrow at 10:00 AM",
      notes: "Had a positive introductory call. Requested a follow-up session with technical architects to review API scalability.",
    }
  ];

  const handleCallInitiated = (lead: any) => {
    setActiveCallLead(lead.name);
    setTotalCalls(prev => prev + 1);
    setTimeout(() => {
      setActiveCallLead(null);
    }, 3000);
  };

  const handleStatusChange = (leadId: string, newStatus: string) => {
    if (newStatus === 'Interested') {
      setConversions(prev => prev + 1);
    }
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto p-4 text-left">
      {/* Page Header - H1 (21px, Bold, #111827) */}
      <div className="bg-white border border-[#E5E7EB] rounded-[14px] shadow-[0_2px_10px_rgba(15,23,42,0.08)] p-6">
        <h1 className="text-[21px] font-bold text-[#111827] leading-tight flex items-center gap-2">
          <Palette className="w-6 h-6 text-[#586EF0]" />
          ManageMyMarketing UI Style Guide Showcase
        </h1>
        {/* Body Text - 11px, #6B7280, leading 1.35 */}
        <p className="text-[11px] text-[#6B7280] leading-[1.35] mt-2">
          This preview module presents UI components built in strict accordance with the project's official design tokens, typography scale, and layout structure.
        </p>
      </div>

      {/* Grid of components */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Style Guide Token Specs */}
        <div className="bg-white border border-[#E5E7EB] rounded-[14px] shadow-[0_2px_10px_rgba(15,23,42,0.08)] p-5 space-y-4">
          <div>
            <h2 className="text-[17px] font-bold text-[#111827]">Design Tokens</h2>
            <p className="text-[11px] text-[#6B7280] leading-[1.35] mt-1">Design system tokens and color mapping rules.</p>
          </div>

          <div className="space-y-3.5 pt-2">
            {/* Color Swatches */}
            <div className="space-y-2">
              <span className="text-[10px] font-semibold text-[#374151] tracking-[0.5px] uppercase block">Color Palette</span>
              <div className="grid grid-cols-2 gap-2 text-[10px] font-bold">
                <div className="flex items-center gap-2 p-2 bg-[#F4F7FA] rounded-[10px] border border-slate-100">
                  <div className="w-5 h-5 rounded-[6px] bg-[#586EF0] shrink-0" />
                  <div className="truncate">
                    <span className="block text-[#111827]">Primary Theme</span>
                    <span className="text-[8px] text-[#6B7280] font-mono">#586EF0</span>
                  </div>
                </div>
                <div className="flex items-center gap-2 p-2 bg-[#F4F7FA] rounded-[10px] border border-slate-100">
                  <div className="w-5 h-5 rounded-[6px] bg-[#7084F4] shrink-0" />
                  <div className="truncate">
                    <span className="block text-[#111827]">Primary Hover</span>
                    <span className="text-[8px] text-[#6B7280] font-mono">#7084F4</span>
                  </div>
                </div>
                <div className="flex items-center gap-2 p-2 bg-[#F4F7FA] rounded-[10px] border border-slate-100">
                  <div className="w-5 h-5 rounded-[6px] bg-[#10B981] shrink-0" />
                  <div className="truncate">
                    <span className="block text-[#111827]">Success</span>
                    <span className="text-[8px] text-[#6B7280] font-mono">#10B981</span>
                  </div>
                </div>
                <div className="flex items-center gap-2 p-2 bg-[#F4F7FA] rounded-[10px] border border-slate-100">
                  <div className="w-5 h-5 rounded-[6px] bg-[#EF4444] shrink-0" />
                  <div className="truncate">
                    <span className="block text-[#111827]">Danger</span>
                    <span className="text-[8px] text-[#6B7280] font-mono">#EF4444</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Typography Scales */}
            <div className="space-y-2 pt-2 border-t border-[#D9DBE1]">
              <span className="text-[10px] font-semibold text-[#374151] tracking-[0.5px] uppercase block">Typography Rules</span>
              <div className="space-y-2 bg-[#F4F7FA] p-3 rounded-[10px] border border-slate-100">
                <div className="border-b border-slate-200/60 pb-1.5">
                  <span className="text-[8px] font-bold text-[#6B7280] block font-mono">H1 / Page Title (21px, Bold)</span>
                  <span className="text-[21px] font-bold text-[#111827] leading-tight block">ManageMyMarketing</span>
                </div>
                <div className="border-b border-slate-200/60 pb-1.5">
                  <span className="text-[8px] font-bold text-[#6B7280] block font-mono">H2 / Card Title (17px, Bold)</span>
                  <span className="text-[17px] font-bold text-[#111827] block">Campaign Overview</span>
                </div>
                <div>
                  <span className="text-[8px] font-bold text-[#6B7280] block font-mono">Body / Description (11px, Line-height 1.35)</span>
                  <span className="text-[11px] text-[#6B7280] leading-[1.35] block">
                    Every design element has been structured around modern corporate blue visual identity models to optimize density.
                  </span>
                </div>
              </div>
            </div>

            {/* Radius and Borders */}
            <div className="space-y-2 pt-2 border-t border-[#D9DBE1]">
              <span className="text-[10px] font-semibold text-[#374151] tracking-[0.5px] uppercase block">Borders & Radius</span>
              <ul className="text-[11px] text-[#6B7280] space-y-1.5 pl-4 list-disc">
                <li>Cards = <strong className="text-[#111827]">14px</strong> (<code className="bg-slate-100 px-1 py-0.5 rounded font-mono text-[9px]">rounded-[14px]</code>)</li>
                <li>Buttons/Inputs = <strong className="text-[#111827]">10px</strong> (<code className="bg-slate-100 px-1 py-0.5 rounded font-mono text-[9px]">rounded-[10px]</code>)</li>
                <li>Shadows = <strong className="text-[#111827]">shadow-card</strong> (<code className="bg-slate-100 px-1 py-0.5 rounded font-mono text-[9px]">shadow-[0_2px_10px_rgba(15,23,42,0.08)]</code>)</li>
              </ul>
            </div>
          </div>
        </div>

        {/* Center/Right Columns: Interactive Showcase */}
        <div className="lg:col-span-2 space-y-6">
          {/* Active Call alert box */}
          {activeCallLead && (
            <div className="bg-amber-500/10 border border-amber-500/25 rounded-[10px] p-3 flex items-center justify-between text-[11px] text-amber-800 animate-pulse">
              <div className="flex items-center gap-2">
                <PhoneCall className="w-4 h-4 text-amber-500 animate-bounce" />
                <span>Simulating active telemarketing call connection to <strong>{activeCallLead}</strong>...</span>
              </div>
              <span className="text-[9px] uppercase font-bold tracking-widest bg-amber-500 text-white px-2 py-0.5 rounded-[10px]">CONNECTED</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Telemarketing Lead Cards */}
            <div className="space-y-4 md:col-span-1">
              <div className="flex items-center justify-between">
                <h2 className="text-[17px] font-bold text-[#111827]">Telemarketing Lead Cards</h2>
                <span className="text-[10px] text-[#9CA3AF]">Interactive Module</span>
              </div>
              {mockLeads.map((lead) => (
                <TelemarketingLeadCard
                  key={lead.id}
                  lead={lead}
                  onCallInitiated={handleCallInitiated}
                  onStatusChange={handleStatusChange}
                />
              ))}
            </div>

            {/* Campaign Widget & Live Feedback */}
            <div className="space-y-4 md:col-span-1">
              <div className="flex items-center justify-between">
                <h2 className="text-[17px] font-bold text-[#111827]">Campaign Analytics Widget</h2>
                <span className="text-[10px] text-[#9CA3AF]">Live Stats</span>
              </div>
              
              <CampaignAnalyticsWidget
                campaignName="Q3 Premium Cloud Migration"
                metrics={[
                  {
                    name: "Total Outbound Calls",
                    value: totalCalls.toLocaleString(),
                    change: "+12.4%",
                    isPositive: true,
                    icon: PhoneCall,
                    iconBg: "bg-[#586EF0]/10",
                    iconColor: "text-[#586EF0]"
                  },
                  {
                    name: "Leads Converted",
                    value: conversions.toLocaleString(),
                    change: "+8.3%",
                    isPositive: true,
                    icon: Check,
                    iconBg: "bg-[#10B981]/10",
                    iconColor: "text-[#10B981]"
                  },
                  {
                    name: "SLA Warnings",
                    value: "4",
                    change: "-50.0%",
                    isPositive: true,
                    icon: AlertTriangle,
                    iconBg: "bg-[#EF4444]/10",
                    iconColor: "text-[#EF4444]"
                  }
                ]}
              />

              {/* Extra context widget showing rules */}
              <div className="bg-[#F4F7FA] border border-[#E5E7EB] rounded-[14px] p-5 space-y-3">
                <h3 className="text-[12px] font-semibold text-[#374151] tracking-[0.5px] uppercase">
                  Design Verification Rules
                </h3>
                <ul className="text-[11px] text-[#6B7280] space-y-2 leading-relaxed">
                  <li className="flex items-start gap-1.5">
                    <span className="w-1.5 h-1.5 rounded-full bg-[#586EF0] mt-1 shrink-0" />
                    <span>Every button matches radius <code className="font-mono text-[9px] bg-white px-1 border">10px</code> and color <code className="font-mono text-[9px] bg-white px-1 border">#586EF0</code>.</span>
                  </li>
                  <li className="flex items-start gap-1.5">
                    <span className="w-1.5 h-1.5 rounded-full bg-[#10B981] mt-1 shrink-0" />
                    <span>Status badge uses green <code className="font-mono text-[9px] bg-white px-1 border">#10B981</code> with opacity wrapper.</span>
                  </li>
                  <li className="flex items-start gap-1.5">
                    <span className="w-1.5 h-1.5 rounded-full bg-[#EF4444] mt-1 shrink-0" />
                    <span>Decline/trash action maps to danger <code className="font-mono text-[9px] bg-white px-1 border">#EF4444</code>.</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MarketingShowcase;
