import React from 'react';
import { Megaphone, PhoneCall, CheckCircle2, AlertTriangle, TrendingUp, Users, ArrowUpRight } from 'lucide-react';

interface CampaignMetric {
  name: string;
  value: string;
  change: string;
  isPositive: boolean;
  icon: React.ComponentType<any>;
  iconBg: string;
  iconColor: string;
}

interface CampaignAnalyticsWidgetProps {
  campaignName?: string;
  status?: 'Running' | 'Paused' | 'Completed';
  metrics?: CampaignMetric[];
  progress?: number;
}

export const CampaignAnalyticsWidget: React.FC<CampaignAnalyticsWidgetProps> = ({
  campaignName = "Q3 Enterprise Telemarketing",
  status = "Running",
  progress = 68,
  metrics = [
    {
      name: "Total Outbound Calls",
      value: "1,248",
      change: "+12.4%",
      isPositive: true,
      icon: PhoneCall,
      iconBg: "bg-[#586EF0]/10",
      iconColor: "text-[#586EF0]"
    },
    {
      name: "Leads Converted",
      value: "342",
      change: "+8.3%",
      isPositive: true,
      icon: CheckCircle2,
      iconBg: "bg-[#10B981]/10",
      iconColor: "text-[#10B981]"
    },
    {
      name: "SLA Warnings",
      value: "4",
      change: "-50.0%",
      isPositive: true, // down is good for warnings
      icon: AlertTriangle,
      iconBg: "bg-[#EF4444]/10",
      iconColor: "text-[#EF4444]"
    }
  ]
}) => {
  return (
    <div className="bg-white border border-[#E5E7EB] rounded-[14px] shadow-[0_2px_10px_rgba(15,23,42,0.08)] p-5 max-w-md w-full relative overflow-hidden text-left flex flex-col justify-between h-[320px]">
      
      {/* Top Header section */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-[10px] bg-[#586EF0]/10 flex items-center justify-center text-[#586EF0]">
              <TrendingUp className="w-3.5 h-3.5" />
            </div>
            {/* Subtitle - 12px, semi-bold, uppercase, letter-spacing 0.5px */}
            <h3 className="text-[12px] font-semibold text-[#374151] tracking-[0.5px] uppercase">
              Campaign Performance
            </h3>
          </div>

          {/* Active status indicator pulse */}
          <div className="flex items-center gap-1.5 bg-slate-50 border border-slate-100 px-2 py-0.5 rounded-[10px]">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#10B981] opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-[#10B981]"></span>
            </span>
            <span className="text-[9px] font-bold text-[#6B7280] uppercase tracking-wider">{status}</span>
          </div>
        </div>

        {/* Card Title - H2 (17px, Bold, #111827) */}
        <h2 className="text-[17px] font-bold text-[#111827] leading-tight mb-4 flex items-center gap-2">
          <Megaphone className="w-4 h-4 text-[#586EF0]" />
          {campaignName}
        </h2>

        {/* Metrics Grid */}
        <div className="grid grid-cols-3 gap-2">
          {metrics.map((metric, i) => (
            <div key={i} className="bg-[#F4F7FA] border border-slate-100 rounded-[10px] p-2.5 flex flex-col justify-between h-[85px] transition-all hover:border-[#586EF0]/20 hover:bg-slate-50/50">
              <div className="flex items-center justify-between">
                <div className={`w-6 h-6 rounded-[8px] ${metric.iconBg} flex items-center justify-center ${metric.iconColor}`}>
                  <metric.icon className="w-3 h-3" />
                </div>
                <span className={`text-[9px] font-bold ${metric.isPositive ? 'text-[#10B981]' : 'text-[#EF4444]'}`}>
                  {metric.change}
                </span>
              </div>
              <div className="mt-1.5">
                {/* Metric Value */}
                <div className="text-[15px] font-extrabold text-[#111827] leading-none mb-0.5">
                  {metric.value}
                </div>
                {/* Metric Name - 9px / 10px */}
                <div className="text-[9px] font-medium text-[#6B7280] leading-tight truncate">
                  {metric.name}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Progress & Target Section */}
      <div className="space-y-3 mt-4">
        {/* Progress Bar Header */}
        <div className="flex justify-between items-center text-[10px] font-bold text-[#6B7280] uppercase tracking-wider">
          <span>Target Progress</span>
          <span className="text-[#586EF0] font-extrabold">{progress}%</span>
        </div>

        {/* Progress Bar Container */}
        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
          <div 
            className="bg-gradient-to-r from-[#586EF0] to-[#7084F4] h-full rounded-full transition-all duration-500" 
            style={{ width: `${progress}%` }}
          />
        </div>

        {/* Footer actions */}
        <div className="flex items-center justify-between pt-3 border-t border-[#D9DBE1]">
          {/* Active stats details - 11px, #6B7280 */}
          <span className="text-[11px] text-[#6B7280] leading-[1.35] flex items-center gap-1">
            <Users className="w-3.5 h-3.5 text-[#9CA3AF]" />
            Active Dialers: <strong className="text-[#374151]">12 Agents</strong>
          </span>

          {/* Button: rounded-button (10px), bg-primary (#586EF0), hover:bg-primary-hover (#7084F4) */}
          <button className="px-3.5 py-2 text-[11px] font-semibold text-white bg-[#586EF0] hover:bg-[#7084F4] rounded-[10px] transition-colors duration-150 flex items-center gap-1 shadow-sm cursor-pointer">
            View Analytics
            <ArrowUpRight className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
};
