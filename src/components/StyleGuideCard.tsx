import React from 'react';
import { PhoneCall, ArrowRight, ShieldCheck } from 'lucide-react';

interface StyleGuideCardProps {
  title?: string;
  subtitle?: string;
  description?: string;
  role?: string;
  isActive?: boolean;
}

export const StyleGuideCard: React.FC<StyleGuideCardProps> = ({
  title = "Active Calling Session",
  subtitle = "Telemarketing Campaign",
  description = "Agent is currently in an active callback session with a prospective client. Ensure quality compliance.",
  role = "Senior Operator",
  isActive = true,
}) => {
  return (
    <div className="bg-[#F4F7FA] p-6 min-h-[200px] flex items-center justify-center">
      {/* 
        Standard Card:
        - shadow-card: 0 2px 10px rgba(15, 23, 42, 0.08)
        - rounded-card: borderRadius 14px
        - border-gray-border: border color #E5E7EB
      */}
      <div className="bg-white border border-[#E5E7EB] rounded-[14px] shadow-[0_2px_10px_rgba(15,23,42,0.08)] p-5 max-w-sm w-full transition-all duration-200 hover:-translate-y-0.5">
        
        {/* Header Section */}
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            {/* Lucide Icon Wrapper with Primary Theme Color (#586EF0) */}
            <div className="w-8 h-8 rounded-[10px] bg-[#586EF0]/10 flex items-center justify-center text-[#586EF0]">
              <PhoneCall className="w-4 h-4" />
            </div>
            <div>
              {/* H3 Section Title: 12px, Semi-bold, #374151, Letter Spacing 0.5px */}
              <h3 className="text-[12px] font-semibold text-[#374151] tracking-[0.5px] uppercase">
                {subtitle}
              </h3>
            </div>
          </div>
          
          {/* Success Banner Badge */}
          {isActive && (
            <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-[10px] text-[8px] font-bold bg-[#10B981]/15 text-[#10B981] uppercase tracking-wider">
              <ShieldCheck className="w-2.5 h-2.5" />
              Active
            </span>
          )}
        </div>

        {/* Content Section */}
        <div className="space-y-2">
          {/* H2 Card Title: 17px, Bold, #111827 */}
          <h2 className="text-[17px] font-bold text-[#111827] leading-tight">
            {title}
          </h2>
          
          {/* Paragraph / Body Text: 11px, #6B7280, Line Height 1.35 */}
          <p className="text-[11px] text-[#6B7280] leading-[1.35]">
            {description}
          </p>
        </div>

        {/* Divider line: border-gray-divider (#D9DBE1) */}
        <div className="border-t border-[#D9DBE1] my-4" />

        {/* Footer Actions */}
        <div className="flex items-center justify-between">
          {/* Role text using Custom Scale 'xs-role' (10px) */}
          <span className="text-[10px] font-medium text-[#9CA3AF]">
            {role}
          </span>

          {/* Button: rounded-button (10px), bg-primary (#586EF0), hover:bg-primary-hover (#7084F4) */}
          <button className="px-3.5 py-2 text-[11px] font-semibold text-white bg-[#586EF0] hover:bg-[#7084F4] rounded-[10px] transition-colors duration-150 flex items-center gap-1.5 shadow-sm cursor-pointer">
            View Details
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
};
