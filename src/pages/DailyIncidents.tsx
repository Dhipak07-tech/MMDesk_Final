import React, { useState, useEffect } from "react";
import { CalendarDays, CheckCircle2, ExternalLink, X, AlertTriangle, Clock, ShieldCheck, Ticket, ChevronLeft, ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";
import { cn } from "@/lib/utils";

interface DailyTicket {
  id: string;
  number: string;
  title: string;
  priority: 'Critical' | 'High' | 'Medium' | 'Low' | 'Very Low';
  status: string;
  members: string[];
  duration: string;
  assignedTime: Date;
  dueTime: Date;
  date: Date;
  description: string;
  comments: { author: string; text: string }[];
  attachments: string[];
  department: string;
}

export function DailyIncidents() {
  const [time, setTime] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [selectedTicket, setSelectedTicket] = useState<DailyTicket | null>(null);

  // Calendar popover state
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const [viewDate, setViewDate] = useState(selectedDate);

  // Synchronize viewDate when selectedDate changes
  useEffect(() => {
    setViewDate(selectedDate);
  }, [selectedDate]);

  // Update time automatically for SLA countdowns
  useEffect(() => {
    const timer = setInterval(() => {
      setTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  // Format Date & Day separately for the header layout
  const getFormattedDateParts = (d: Date) => {
    const day = d.getDate();
    const months = [
      "January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"
    ];
    const month = months[d.getMonth()];
    const year = d.getFullYear();
    const days = [
      "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    ];
    const dayName = days[d.getDay()];

    return {
      dateStr: `${day} ${month} ${year}`,
      dayStr: dayName
    };
  };

  const { dateStr: formattedDateOnly, dayStr: formattedDayOnly } = getFormattedDateParts(selectedDate);

  // Helper for rendering days in month grid
  const getDaysInMonthGrid = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth();

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const totalDaysInMonth = new Date(year, month + 1, 0).getDate();

    const days: (Date | null)[] = [];

    // Empty padding slots
    for (let i = 0; i < firstDayOfMonth; i++) {
      days.push(null);
    }

    // Month days
    for (let d = 1; d <= totalDaysInMonth; d++) {
      days.push(new Date(year, month, d));
    }

    return days;
  };

  const handlePrevMonth = (e: React.MouseEvent) => {
    e.stopPropagation();
    setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() - 1, 1));
  };

  const handleNextMonth = (e: React.MouseEvent) => {
    e.stopPropagation();
    setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 1));
  };

  const handleSelectDate = (date: Date) => {
    setSelectedDate(date);
    setIsCalendarOpen(false);
  };

  const handleJumpToday = (e: React.MouseEvent) => {
    e.stopPropagation();
    const today = new Date();
    setSelectedDate(today);
    setViewDate(today);
    setIsCalendarOpen(false);
  };

  // Generate dynamic mock tickets relative to current state
  const getTicketsForDate = (date: Date) => {
    const isToday = date.toDateString() === new Date().toDateString();
    const baseTime = isToday ? new Date() : new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12, 0);

    const getDueTime = (assignedTime: Date, priority: string) => {
      const timeObj = new Date(assignedTime.getTime());
      switch (priority) {
        case "Critical":
          return new Date(timeObj.getTime() + 30 * 60000);
        case "High":
          return new Date(timeObj.getTime() + 60 * 60000);
        case "Medium":
          return new Date(timeObj.getTime() + 180 * 60000);
        case "Low":
          return new Date(timeObj.getTime() + 360 * 60000);
        case "Very Low":
          return new Date(timeObj.getTime() + 1440 * 60000);
        default:
          return new Date(timeObj.getTime() + 360 * 60000);
      }
    };

    const rawTickets = [
      {
        id: "1",
        number: "INC-9482103",
        title: "Core database latency spike in AP-East cluster",
        priority: "Critical" as const,
        status: "In Progress",
        members: ["Arun G", "Dhipak"],
        duration: "45m",
        assignedTime: new Date(baseTime.getTime() - 45 * 60000),
        date: date,
        description: "Core database performance has degraded with read queries experiencing high latency. Primary replica index page locks are suspected.",
        comments: [
          { author: "Arun G", text: "Verified replica instance locks. Rebuilding statistics..." }
        ],
        attachments: ["db_stats_ap_east.log"],
        department: "Database Administration"
      },
      {
        id: "2",
        number: "INC-8374921",
        title: "High memory utilization on production web server #4",
        priority: "High" as const,
        status: "New",
        members: ["Swedha"],
        duration: "1h 15m",
        assignedTime: new Date(baseTime.getTime() - 15 * 60000),
        date: date,
        description: "Memory load exceeded 92% alert threshold. Apache worker count reached max capacity.",
        comments: [],
        attachments: [],
        department: "Infrastructure Systems"
      },
      {
        id: "3",
        number: "INC-7489212",
        title: "SMTP relay certificate expiration warning",
        priority: "Medium" as const,
        status: "Open",
        members: [],
        duration: "2h 30m",
        assignedTime: new Date(baseTime.getTime() - 120 * 60000),
        date: date,
        description: "The SSL certificate for the mail relay gateway will expire in 3 days.",
        comments: [],
        attachments: ["cert_details.pem"],
        department: "Security Operations"
      },
      {
        id: "4",
        number: "INC-6392019",
        title: "VPN client configuration sync error for remote staff",
        priority: "Low" as const,
        status: "Resolved",
        members: ["Support Agent"],
        duration: "3h 10m",
        assignedTime: new Date(baseTime.getTime() - 4 * 60 * 60000),
        date: date,
        description: "Remote access VPN client profiles failing to synchronize split-tunnel rules.",
        comments: [
          { author: "Support Agent", text: "Re-pushed profile definitions to gateway controller. Confirmed resolution." }
        ],
        attachments: [],
        department: "User Services Support"
      },
      {
        id: "5",
        number: "INC-5283910",
        title: "Request for standard development IDE license renewal",
        priority: "Very Low" as const,
        status: "Closed",
        members: ["Support Agent", "Arun G"],
        duration: "5h 20m",
        assignedTime: new Date(baseTime.getTime() - 5 * 60 * 60000),
        date: date,
        description: "License purchase request for JetBrains IDE keys for development team onboarding.",
        comments: [],
        attachments: [],
        department: "Procurement / IT Asset"
      },
      {
        id: "6",
        number: "INC-9582039",
        title: "SSO authentication failure on internal dashboard",
        priority: "Critical" as const,
        status: "In Progress",
        members: ["Dhipak"],
        duration: "15m",
        assignedTime: new Date(baseTime.getTime() - 25 * 60000),
        date: date,
        description: "SAML assertion token validation failing. Users getting unauthorized error page.",
        comments: [
          { author: "Dhipak", text: "Identity Provider configuration mismatch found. Fixing cert metadata." }
        ],
        attachments: ["saml_trace_log.txt"],
        department: "Cybersecurity & Identity"
      },
      {
        id: "7",
        number: "INC-8493019",
        title: "API Gateway rate-limiting block false-positives",
        priority: "High" as const,
        status: "Pending Approval",
        members: ["Swedha", "Arun G"],
        duration: "50m",
        assignedTime: new Date(baseTime.getTime() - 65 * 60000),
        date: date,
        description: "WAF rate-limiting rule triggered by normal client pagination requests.",
        comments: [],
        attachments: [],
        department: "API Middleware Dev"
      }
    ].map(t => {
      const dueTime = getDueTime(t.assignedTime, t.priority);
      return {
        ...t,
        dueTime
      };
    });

    const priorityRank = {
      "Critical": 1,
      "High": 2,
      "Medium": 3,
      "Low": 4,
      "Very Low": 5
    };

    return rawTickets.sort((a, b) => {
      const rankA = priorityRank[a.priority];
      const rankB = priorityRank[b.priority];
      if (rankA !== rankB) {
        return rankA - rankB;
      }
      return a.dueTime.getTime() - b.dueTime.getTime();
    });
  };

  const tickets = getTicketsForDate(selectedDate);

  const getPriorityBadgeClass = (p: string) => {
    switch (p) {
      case "Critical":
        return "bg-red-500/10 text-red-500 border border-red-500/20 text-xs px-2 py-0.5 font-black rounded-full";
      case "High":
        return "bg-orange-500/10 text-orange-500 border border-orange-500/20 text-xs px-2 py-0.5 font-black rounded-full";
      case "Medium":
        return "bg-amber-500/10 text-amber-500 border border-amber-500/20 text-xs px-2 py-0.5 font-black rounded-full";
      case "Low":
        return "bg-blue-500/10 text-blue-500 border border-blue-500/20 text-xs px-2 py-0.5 font-black rounded-full";
      case "Very Low":
        return "bg-slate-500/10 text-slate-500 border border-slate-500/20 text-xs px-2 py-0.5 font-black rounded-full";
      default:
        return "bg-blue-50 text-blue-700 border border-blue-100 text-xs px-2 py-0.5 font-black rounded-full";
    }
  };

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case "New":
        return "bg-sky-500/10 text-sky-500 border border-sky-500/25 text-xs px-2 py-0.5 font-bold rounded-full";
      case "In Progress":
        return "bg-indigo-500/10 text-indigo-500 border border-indigo-500/25 text-xs px-2 py-0.5 font-bold rounded-full";
      case "Resolved":
      case "Closed":
      case "Completed":
        return "bg-emerald-500/10 text-emerald-500 border border-emerald-500/25 text-xs px-2 py-0.5 font-bold rounded-full";
      case "Pending Approval":
      case "On Hold":
      case "Open":
      default:
        return "bg-slate-500/10 text-slate-500 border border-slate-500/25 text-xs px-2 py-0.5 font-bold rounded-full";
    }
  };

  const getDueTimeStatus = (ticket: DailyTicket, now: Date) => {
    if (['Resolved', 'Closed', 'Completed'].includes(ticket.status)) {
      return { color: 'gray', text: 'Completed' };
    }
    const diffMs = ticket.dueTime.getTime() - now.getTime();
    const diffMins = Math.round(diffMs / 60000);

    if (diffMins < 0) {
      return { color: 'red', text: 'Overdue' };
    } else if (diffMins <= 15) {
      return { color: 'yellow', text: 'Approaching' };
    } else {
      return { color: 'green', text: 'Sufficient Time' };
    }
  };

  const getDueTimeBadgeClass = (color: string) => {
    switch (color) {
      case "green":
        return "bg-emerald-500/15 text-emerald-500 border border-emerald-500/30 text-[9px] font-extrabold rounded-full";
      case "yellow":
        return "bg-amber-500/15 text-amber-500 border border-amber-500/30 text-[9px] font-extrabold rounded-full animate-pulse";
      case "red":
        return "bg-red-500/15 text-red-500 border border-red-500/30 text-[9px] font-extrabold rounded-full";
      case "gray":
      default:
        return "bg-slate-500/15 text-slate-500 border border-slate-500/30 text-[9px] font-extrabold rounded-full";
    }
  };

  // Status Metrics calculations
  const onTrackCount = tickets.filter(t => {
    const status = getDueTimeStatus(t, time);
    return status.color === 'green';
  }).length;

  const overdueCount = tickets.filter(t => {
    const status = getDueTimeStatus(t, time);
    return status.color === 'red';
  }).length;

  // Dynamic Date-aware Formatter
  const formatAssignedDueTime = (assignedTime: Date, dueTime: Date) => {
    const isSameDay = assignedTime.toDateString() === dueTime.toDateString();
    const formatTimeOnly = (d: Date) => {
      return d.toLocaleTimeString(undefined, {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      });
    };
    const formatDateAndTime = (d: Date) => {
      const day = d.getDate();
      const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
      const month = months[d.getMonth()];
      const timeStr = d.toLocaleTimeString(undefined, {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      });
      return `${day} ${month}, ${timeStr}`;
    };

    if (isSameDay) {
      return {
        assigned: formatTimeOnly(assignedTime),
        due: formatTimeOnly(dueTime)
      };
    } else {
      return {
        assigned: formatDateAndTime(assignedTime),
        due: formatDateAndTime(dueTime)
      };
    }
  };

  return (
    <div className="standard-page-layout space-y-6 min-h-screen bg-background text-foreground p-3 sm:p-5 lg:p-6 animate-fade-in">
      
      {/* ── Enterprise Header Section ── */}
      <div className="bg-card border border-border/60 rounded-2xl p-4 sm:p-5 shadow-md overflow-visible relative z-30">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-5">
          
          {/* Left: Entire Clickable Calendar Card with Popover */}
          <div className="relative">
            <div 
              onClick={() => setIsCalendarOpen(!isCalendarOpen)}
              className="flex items-center gap-3.5 cursor-pointer group select-none hover:bg-muted/40 p-2 px-3 rounded-2xl transition-all border border-transparent hover:border-border/60"
              title="Click anywhere to open date picker"
            >
              <div className="w-13 h-13 rounded-2xl bg-blue-500/10 group-hover:bg-blue-500/20 border border-blue-500/25 text-blue-500 flex items-center justify-center transition-all shadow-sm shrink-0 group-hover:scale-105 active:scale-95 p-3">
                <CalendarDays className="w-6 h-6" />
              </div>
              
              <div>
                <h1 className="text-xl sm:text-2xl font-black text-foreground tracking-tight whitespace-nowrap">
                  {formattedDateOnly}
                </h1>
                <p className="text-xs sm:text-sm font-extrabold text-blue-500 dark:text-blue-400 mt-0.5">
                  {formattedDayOnly}
                </p>
              </div>
            </div>

            {/* Custom Interactive Date Picker Popover */}
            {isCalendarOpen && (
              <>
                <div 
                  className="fixed inset-0 z-40 bg-black/10 backdrop-blur-[1px]" 
                  onClick={() => setIsCalendarOpen(false)} 
                />

                <div className="absolute top-full left-0 mt-2 z-50 bg-card border border-border/80 rounded-2xl shadow-2xl p-4 w-[280px] sm:w-[310px] animate-scale-up">
                  {/* Calendar Header: Prev / Month Year / Next */}
                  <div className="flex items-center justify-between mb-3 pb-2 border-b border-border/40">
                    <button
                      onClick={handlePrevMonth}
                      className="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors"
                      title="Previous Month"
                    >
                      <ChevronLeft className="w-4 h-4" />
                    </button>
                    
                    <span className="font-extrabold text-xs sm:text-sm text-foreground">
                      {viewDate.toLocaleDateString(undefined, { month: 'long', year: 'numeric' })}
                    </span>

                    <button
                      onClick={handleNextMonth}
                      className="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors"
                      title="Next Month"
                    >
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>

                  {/* Day of Week Headers */}
                  <div className="grid grid-cols-7 text-center text-[10px] font-bold uppercase tracking-wider text-muted-foreground mb-2">
                    <span>Su</span>
                    <span>Mo</span>
                    <span>Tu</span>
                    <span>We</span>
                    <span>Th</span>
                    <span>Fr</span>
                    <span>Sa</span>
                  </div>

                  {/* Month Days Grid */}
                  <div className="grid grid-cols-7 gap-1 text-center text-xs">
                    {getDaysInMonthGrid(viewDate).map((d, index) => {
                      if (!d) {
                        return <div key={`empty-${index}`} className="p-2" />;
                      }

                      const isSelected = d.toDateString() === selectedDate.toDateString();
                      const isToday = d.toDateString() === new Date().toDateString();

                      return (
                        <button
                          key={d.toISOString()}
                          onClick={() => handleSelectDate(d)}
                          className={cn(
                            "p-2 w-8 h-8 mx-auto flex items-center justify-center rounded-xl font-bold transition-all duration-150 text-xs",
                            isSelected
                              ? "bg-blue-500 text-white shadow-md shadow-blue-500/20"
                              : isToday
                              ? "bg-blue-500/10 text-blue-500 border border-blue-500/30 font-black"
                              : "hover:bg-muted text-foreground"
                          )}
                        >
                          {d.getDate()}
                        </button>
                      );
                    })}
                  </div>

                  {/* Today Action Footer */}
                  <div className="mt-3 pt-2 border-t border-border/40 flex items-center justify-between">
                    <button
                      onClick={handleJumpToday}
                      className="text-xs font-bold text-blue-500 hover:text-blue-600 hover:underline transition-colors flex items-center gap-1"
                    >
                      Go to Today
                    </button>
                    <button
                      onClick={() => setIsCalendarOpen(false)}
                      className="text-xs font-semibold text-muted-foreground hover:text-foreground"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              </>
            )}
          </div>

          {/* Center & Right Cards Section */}
          <div className="flex items-center gap-4 flex-wrap md:flex-nowrap">
            
            {/* Total Incidents Summary Card */}
            <div className="bg-card border border-border/60 rounded-2xl p-3.5 px-4 flex items-center gap-3.5 shadow-sm border-l-4 border-l-blue-500 flex-1 sm:flex-initial">
              <div className="w-10 h-10 rounded-xl bg-blue-500/10 border border-blue-500/20 text-blue-500 flex items-center justify-center font-bold text-lg shrink-0">
                <Ticket className="w-5 h-5" />
              </div>
              <div>
                <span className="text-[10px] font-extrabold uppercase tracking-wider text-muted-foreground block">Total Incidents</span>
                <div className="flex items-baseline gap-2">
                  <span className="text-xl sm:text-2xl font-black text-foreground">{tickets.length}</span>
                  <span className="text-[10px] text-muted-foreground font-medium whitespace-nowrap">For selected date</span>
                </div>
              </div>
            </div>

            {/* Compact Status Summary Card */}
            <div className="bg-card border border-border/60 rounded-2xl p-3.5 px-4 flex items-center justify-around gap-4 shadow-sm flex-1 sm:flex-initial">
              {/* On Track */}
              <div className="flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse shrink-0" />
                <div>
                  <span className="text-[9px] font-extrabold uppercase tracking-wider text-muted-foreground block">On Track</span>
                  <span className="text-lg font-black text-emerald-500">{onTrackCount}</span>
                </div>
              </div>
              
              <div className="h-7 w-[1px] bg-border/60" />
              
              {/* Overdue */}
              <div className="flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-red-500 animate-pulse shrink-0" />
                <div>
                  <span className="text-[9px] font-extrabold uppercase tracking-wider text-muted-foreground block">Overdue</span>
                  <span className="text-lg font-black text-red-500">{overdueCount}</span>
                </div>
              </div>
            </div>

          </div>

        </div>
      </div>

      {/* ── Priority Information Bar ── */}
      <div className="bg-muted/20 border border-border/50 rounded-2xl p-3.5 px-4 flex flex-col xl:flex-row xl:items-center justify-between gap-3 shadow-sm text-xs">
        {/* Priority SLA Due Time Rules */}
        <div className="flex flex-wrap items-center gap-2 font-medium">
          <span className="font-extrabold uppercase tracking-wider text-foreground text-[11px] mr-1">
            Priority Due Time — Starts from Assigned Time:
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-black bg-red-500/10 text-red-500 border border-red-500/20">
            Critical → 30m
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-black bg-orange-500/10 text-orange-500 border border-orange-500/20">
            High → 1h
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-black bg-amber-500/10 text-amber-500 border border-amber-500/20">
            Medium → 3h
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-black bg-blue-500/10 text-blue-500 border border-blue-500/20">
            Low → 6h
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-black bg-slate-500/10 text-slate-500 border border-slate-500/20">
            Very Low → 24h
          </span>
        </div>

        {/* Escalation Reminder Message */}
        <div className="flex items-center gap-2 text-[11px] sm:text-xs font-bold text-amber-500 dark:text-amber-400 bg-amber-500/10 border border-amber-500/20 px-3 py-1 rounded-xl shrink-0">
          <AlertTriangle className="w-3.5 h-3.5 shrink-0" />
          <span>Resolve incidents within the due time to avoid escalation.</span>
        </div>
      </div>

      {/* ── Daily Incident Table Container ── */}
      <div className="bg-card border border-border/80 shadow-xl rounded-2xl overflow-hidden">
        <div className="p-4 px-5 border-b border-border/50 bg-muted/10 flex items-center justify-between">
          <div>
            <h2 className="text-xs sm:text-sm font-black text-foreground tracking-widest uppercase">Daily Incident Stream</h2>
            <p className="text-[11px] text-muted-foreground mt-0.5">Real-time help-desk monitoring. All critical parameters & due deadlines visible at a glance.</p>
          </div>
          <span className="text-[10px] font-extrabold uppercase tracking-wider text-emerald-500 bg-emerald-500/10 border border-emerald-500/20 px-2.5 py-1 rounded-full hidden sm:inline-block">
            Due Time Live
          </span>
        </div>

        <div className="w-full overflow-x-auto lg:overflow-x-visible">
          <table className="w-full text-left border-collapse align-middle text-xs sm:text-sm">
            <thead>
              <tr className="bg-muted/20 border-b border-border/60 text-[11px] font-black uppercase tracking-wider text-muted-foreground select-none">
                <th className="py-3 px-2 w-[40px] text-center whitespace-nowrap">S.No</th>
                <th className="py-3 px-2 w-[100px] whitespace-nowrap">Ticket ID</th>
                <th className="py-3 px-2 min-w-[140px] max-w-[200px] lg:max-w-[280px]">Ticket Name</th>
                <th className="py-3 px-2 w-[85px] whitespace-nowrap">Priority</th>
                <th className="py-3 px-2 w-[95px] whitespace-nowrap">Status</th>
                <th className="py-3 px-2 w-[150px] whitespace-nowrap">Members Working</th>
                <th className="py-3 px-2 w-[100px] whitespace-nowrap">Assigned Time</th>
                <th className="py-3 px-2 w-[130px] whitespace-nowrap text-right font-black text-foreground">Due Time</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/40 bg-transparent">
              {tickets.map((ticket, idx) => {
                const dueTimeStatus = getDueTimeStatus(ticket, time);
                const isOverdue = dueTimeStatus.color === "red";
                const { assigned, due } = formatAssignedDueTime(ticket.assignedTime, ticket.dueTime);

                return (
                  <tr
                    key={ticket.id}
                    onClick={() => setSelectedTicket(ticket)}
                    className={cn(
                      "hover:bg-blue-500/5 dark:hover:bg-blue-500/10 transition-all duration-150 cursor-pointer border-l-2 border-l-transparent",
                      isOverdue && "bg-red-500/5 hover:bg-red-500/10 border-l-red-500"
                    )}
                  >
                    {/* S.No */}
                    <td className="py-3 px-2 text-center text-muted-foreground/80 font-semibold whitespace-nowrap">
                      {idx + 1}
                    </td>

                    {/* Ticket ID */}
                    <td className="py-3 px-2 whitespace-nowrap">
                      <span className="font-mono text-xs font-extrabold bg-blue-500/10 text-blue-500 dark:bg-blue-500/20 dark:text-blue-400 px-2 py-0.5 rounded border border-blue-500/15">
                        {ticket.number}
                      </span>
                    </td>

                    {/* Ticket Name (Truncated with tooltip) */}
                    <td className="py-3 px-2 font-bold text-foreground truncate max-w-[140px] md:max-w-[200px] lg:max-w-[280px]" title={ticket.title}>
                      {ticket.title}
                    </td>

                    {/* Priority */}
                    <td className="py-3 px-2 whitespace-nowrap">
                      <span className={cn("tracking-wider font-black select-none border", getPriorityBadgeClass(ticket.priority))}>
                        {ticket.priority}
                      </span>
                    </td>

                    {/* Status */}
                    <td className="py-3 px-2 whitespace-nowrap">
                      <span className={cn("tracking-wider select-none border", getStatusBadgeClass(ticket.status))}>
                        {ticket.status}
                      </span>
                    </td>

                    {/* Members Working */}
                    <td className="py-3 px-2 whitespace-nowrap" onClick={(e) => e.stopPropagation()}>
                      {ticket.members.length > 0 ? (
                        <div className="flex flex-row flex-nowrap gap-1 overflow-x-auto max-w-[150px] scrollbar-none">
                          {ticket.members.map((member, mIdx) => (
                            <span key={mIdx} className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-semibold bg-muted text-foreground border border-border/80 shadow-sm shrink-0">
                              <span className="w-1.5 h-1.5 rounded-full bg-sn-green" />
                              {member}
                            </span>
                          ))}
                        </div>
                      ) : (
                        <span className="text-[10px] text-muted-foreground font-semibold uppercase tracking-wider px-2 py-0.5 rounded-full bg-slate-500/10 border border-slate-500/25">Not Assigned</span>
                      )}
                    </td>

                    {/* Assigned Time */}
                    <td className="py-3 px-2 font-mono text-xs font-semibold text-muted-foreground/90 whitespace-nowrap">
                      {assigned}
                    </td>

                    {/* Due Time */}
                    <td className="py-3 px-2 whitespace-nowrap text-right">
                      <div className="flex flex-col items-end gap-0.5">
                        <span className="font-mono text-xs font-extrabold text-foreground">
                          {due}
                        </span>
                        <span className={cn("px-2 py-0.5 border shadow-sm select-none", getDueTimeBadgeClass(dueTimeStatus.color))}>
                          {dueTimeStatus.text}
                        </span>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* ── Slide-Over Side Panel Details Modal ── */}
      {selectedTicket && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fade-in">
          <div className="bg-card border border-border/60 rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden animate-scale-up">
            {/* Modal Header */}
            <div className="p-5 border-b border-border/60 flex items-center justify-between bg-muted/20">
              <div>
                <span className={cn("tracking-wider font-black select-none border", getPriorityBadgeClass(selectedTicket.priority))}>
                  {selectedTicket.priority} Priority
                </span>
                <h2 className="text-base sm:text-lg font-bold text-foreground mt-3">{selectedTicket.number}: {selectedTicket.title}</h2>
              </div>
              <button
                onClick={() => setSelectedTicket(null)}
                className="p-1.5 hover:bg-muted rounded-lg text-muted-foreground hover:text-foreground transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Modal Content */}
            <div className="p-6 space-y-4 max-h-[65vh] overflow-y-auto">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Date & Day</span>
                  <p className="text-xs sm:text-sm font-medium text-foreground mt-0.5">
                    {selectedTicket.date.toLocaleDateString(undefined, { day: 'numeric', month: 'long', year: 'numeric' })} ({selectedTicket.date.toLocaleDateString(undefined, { weekday: 'long' })})
                  </p>
                </div>
                <div>
                  <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Due Time</span>
                  <p className="text-xs sm:text-sm font-medium text-foreground mt-0.5">
                    {selectedTicket.dueTime.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })}
                  </p>
                </div>
                <div>
                  <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Status</span>
                  <p className="text-xs sm:text-sm font-medium text-foreground mt-0.5">
                    {selectedTicket.status}
                  </p>
                </div>
                <div>
                  <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Department</span>
                  <p className="text-xs sm:text-sm font-medium text-foreground mt-0.5">
                    {selectedTicket.department}
                  </p>
                </div>
              </div>

              <hr className="border-border/40" />

              <div>
                <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Description</span>
                <p className="text-xs sm:text-sm text-foreground mt-1 bg-muted/20 p-3 rounded-lg border border-border/40 leading-relaxed">
                  {selectedTicket.description}
                </p>
              </div>

              <div>
                <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Attachments</span>
                {selectedTicket.attachments.length > 0 ? (
                  <div className="flex flex-wrap gap-2 mt-1.5">
                    {selectedTicket.attachments.map((file) => (
                      <span key={file} className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[10px] sm:text-xs font-medium bg-muted/40 border border-border/40 text-foreground cursor-pointer hover:bg-muted transition-colors">
                        📎 {file}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="text-[10px] sm:text-xs text-muted-foreground italic mt-1">No attachments available.</p>
                )}
              </div>

              <div>
                <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">Discussion / Comments</span>
                {selectedTicket.comments.length > 0 ? (
                  <div className="space-y-2 mt-2">
                    {selectedTicket.comments.map((comment, cIdx) => (
                      <div key={cIdx} className="text-[10px] sm:text-xs bg-muted/20 p-2.5 rounded-lg border border-border/30">
                        <div className="flex items-center justify-between font-bold text-foreground">
                          <span>{comment.author}</span>
                          <span className="text-[8px] sm:text-[9px] text-muted-foreground font-normal">Just now</span>
                        </div>
                        <p className="text-muted-foreground mt-1">{comment.text}</p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-[10px] sm:text-xs text-muted-foreground italic mt-1">No comments posted yet.</p>
                )}
              </div>
            </div>

            {/* Modal Footer */}
            <div className="p-4 border-t border-border/60 flex justify-end bg-muted/10">
              <button
                onClick={() => setSelectedTicket(null)}
                className="px-4 py-2 bg-muted text-foreground text-xs sm:text-sm font-semibold rounded-lg hover:bg-muted/80 transition-colors"
              >
                Close Details
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
