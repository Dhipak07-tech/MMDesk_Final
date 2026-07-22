import { SafeAny } from '@/types';
import api from '@/lib/api';
import React, { useState, useEffect, useCallback, useRef } from"react";
import { BarChart2, ArrowLeft, Bot, Zap, Clock, Plus, Eye, Save, X, Paperclip, Check } from"lucide-react";
import { Link } from"react-router-dom";
import { useAuth } from"../contexts/AuthContext";

function formatLocalDateTimeForInput(dateTimeStr: string | null | undefined): string {
  if (!dateTimeStr) return "";
  try {
    const d = new Date(dateTimeStr);
    if (isNaN(d.getTime())) return "";
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  } catch (e) {
    return "";
  }
}

export function TimesheetReports() {
  const { user, profile } = useAuth();
  const [timesheets, setTimesheets] = useState<any[]>([]);
  const [allCards, setAllCards] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activitySessions, setActivitySessions] = useState<any[]>([]);

  // Delay Reports States
  const [delayReports, setDelayReports] = useState<any[]>([]);
  const [isDelayModalOpen, setIsDelayModalOpen] = useState(false);
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [selectedDelay, setSelectedDelay] = useState<any>(null);
  const [manager, setManager] = useState({ uid: "", name: "" });
  const [managersList, setManagersList] = useState<any[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [uploading, setUploading] = useState(false);

  const [form, setForm] = useState({
    id: "",
    task_incident_id: "",
    assigned_time: "",
    expected_completion_time: "",
    actual_completion_time: "",
    total_delay_duration: "",
    reason_for_delay: "Technical Issues",
    activities_performed_during_delay: "",
    meeting_details: "",
    technical_system_issues: "",
    dependency_approval_wait_time: "",
    additional_comments: "",
    attachment_url: "",
    attachment_name: ""
  });

  // Ticket search states for Log Time Delay Report dialog
  const [allTickets, setAllTickets] = useState<any[]>([]);
  const [isTicketDropdownOpen, setIsTicketDropdownOpen] = useState(false);
  const ticketDropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isDelayModalOpen) {
      const fetchTickets = async () => {
        try {
          const res = await api.get("/api/tickets/all");
          setAllTickets(Array.isArray(res.data) ? res.data : []);
        } catch (e) {
          console.error("Failed to load tickets:", e);
        }
      };
      fetchTickets();
    }
  }, [isDelayModalOpen]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (ticketDropdownRef.current && !ticketDropdownRef.current.contains(event.target as Node)) {
        setIsTicketDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const loadData = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    try {
      // Fetch timesheets
      const tsRes = await api.get(`/api/timesheets?user_id=${user.uid}`);
      const tsList = Array.isArray(tsRes.data) ? tsRes.data : [];
      tsList.sort((a: SafeAny, b: SafeAny) => (b.week_start || "").localeCompare(a.week_start || ""));
      setTimesheets(tsList);

      if (tsList.length > 0) {
        // Fetch all cards for the user
        const tcRes = await api.get(`/api/time-cards?user_id=${user.uid}`);
        const cards = Array.isArray(tcRes.data) ? tcRes.data : [];
        setAllCards(cards);
      }

      // Fetch AI activity sessions
      try {
        const sessRes = await api.get(`/api/activity-sessions?user_id=${user.uid}&limit=20`);
        setActivitySessions(Array.isArray(sessRes.data) ? sessRes.data : []);
      } catch { /* silent */ }

      // Fetch delay reports
      await fetchDelayReports();

    } catch (e) {
      console.error(e);
    }
    setLoading(false);
  }, [user, profile]);

  const fetchDelayReports = async () => {
    if (!user) return;
    try {
      const isSuper = profile?.role === "super_admin" || profile?.role === "ultra_super_admin";
      const url = isSuper 
        ? "/api/delay-reports" 
        : `/api/delay-reports?user_uid=${user.uid}`;
      const res = await api.get(url);
      setDelayReports(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      console.error("Failed to load delay reports", e);
    }
  };

  const fetchManagerInfo = async () => {
    if (!user) return;
    try {
      // Fetch users list first (requires only standard authenticated role)
      const usersRes = await api.get("/api/users");
      const users = Array.isArray(usersRes.data) ? usersRes.data : [];
      const admins = users.filter((u: any) => u.role === "admin" || u.role === "super_admin" || u.role === "ultra_super_admin");
      setManagersList(admins);

      let groupManagerFound = false;
      // Optionally fetch group settings (might fail with 403 if role is standard user)
      try {
        const [groupsRes, membersRes] = await Promise.all([
          api.get("/api/settings_groups"),
          api.get("/api/settings_group_members")
        ]);
        const groups = Array.isArray(groupsRes.data) ? groupsRes.data : [];
        const members = Array.isArray(membersRes.data) ? membersRes.data : [];
        const memberRecord = members.find((m: any) => m.user_id === user.uid);
        if (memberRecord) {
          const groupRecord = groups.find((g: any) => g.id === memberRecord.id || g.name === memberRecord.id);
          if (groupRecord && groupRecord.manager_uid) {
            setManager({ uid: groupRecord.manager_uid, name: groupRecord.manager_name || "Manager" });
            groupManagerFound = true;
          }
        }
      } catch (e) {
        console.warn("Group settings endpoints are restricted or failed: ", e);
      }

      // Fallback to first available admin if group manager wasn't found/resolved
      if (!groupManagerFound) {
        const adminUser = admins.length > 0 ? admins[0] : null;
        if (adminUser) {
          setManager({ uid: adminUser.uid, name: adminUser.name || adminUser.email });
        } else {
          setManager({ uid: "admin_uid", name: "System Administrator" });
        }
      }
    } catch (e) {
      console.error("Error auto-populating line manager:", e);
      setManager({ uid: "admin_uid", name: "System Administrator" });
    }
  };

  const handleManagerChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedUid = e.target.value;
    const selectedUser = managersList.find(m => m.uid === selectedUid);
    if (selectedUser) {
      setManager({ uid: selectedUser.uid, name: selectedUser.name || selectedUser.email });
    }
  };

  const handleApproveDelay = async (id: string) => {
    if (!confirm("Approve this delay report?")) return;
    try {
      const res = await api(`/api/delay-reports/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ report_status: "Approved" })
      });
      if (res.ok) {
        alert("Delay report approved successfully!");
        fetchDelayReports();
      } else {
        alert("Failed to approve delay report.");
      }
    } catch (e) {
      console.error("Error approving delay report", e);
    }
  };

  const handleRejectDelay = async (id: string) => {
    const reason = prompt("Enter reason for rejection/clarification:");
    if (reason === null) return;
    try {
      const res = await api(`/api/delay-reports/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          report_status: "Rejected/Clarification Needed",
          rejection_reason: reason
        })
      });
      if (res.ok) {
        alert("Delay report rejected successfully!");
        fetchDelayReports();
      } else {
        alert("Failed to reject delay report.");
      }
    } catch (e) {
      console.error("Error rejecting delay report", e);
    }
  };

  useEffect(() => {
    loadData();
    fetchManagerInfo();
  }, [loadData]);

  // Recalculate duration automatically when times change
  useEffect(() => {
    if (form.assigned_time && form.actual_completion_time) {
      const start = new Date(form.assigned_time);
      const end = new Date(form.actual_completion_time);
      const diffMs = end.getTime() - start.getTime();
      if (diffMs <= 0) {
        setForm(f => ({ ...f, total_delay_duration: "0 minutes" }));
        return;
      }
      const diffMins = Math.floor(diffMs / 60000);
      const hrs = Math.floor(diffMins / 60);
      const mins = diffMins % 60;
      setForm(f => ({
        ...f,
        total_delay_duration: `${hrs > 0 ? hrs + " hours " : ""}${mins} minutes`
      }));
    }
  }, [form.assigned_time, form.actual_completion_time]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      const uploadData = new FormData();
      uploadData.append("file", file);
      const res = await api("/api/delay-reports/upload", {
        method: "POST",
        body: uploadData
      });
      if (res.ok) {
        const result = await res.json();
        setForm(prev => ({
          ...prev,
          attachment_url: result.file_path,
          attachment_name: result.file_name
        }));
        alert("File attached successfully!");
      } else {
        alert("Failed to upload file.");
      }
    } catch (err) {
      console.error(err);
      alert("Error uploading file.");
    } finally {
      setUploading(false);
    }
  };

  const openDelayModal = () => {
    setForm({
      id: "",
      task_incident_id: "",
      assigned_time: "",
      expected_completion_time: "",
      actual_completion_time: "",
      total_delay_duration: "",
      reason_for_delay: "Technical Issues",
      activities_performed_during_delay: "",
      meeting_details: "",
      technical_system_issues: "",
      dependency_approval_wait_time: "",
      additional_comments: "",
      attachment_url: "",
      attachment_name: ""
    });
    setIsDelayModalOpen(true);
  };

  const handleSaveDelayReport = async (status: "Draft" | "Submitted/Pending Review") => {
    if (!form.task_incident_id || !form.assigned_time || !form.expected_completion_time || !form.actual_completion_time || !form.activities_performed_during_delay) {
      alert("Please fill in all required fields.");
      return;
    }
    setSubmitting(true);
    try {
      const payload = {
        ...form,
        report_status: status,
        employee_uid: user?.uid,
        employee_name: profile?.name || user?.email || "Employee",
        manager_uid: manager.uid,
        manager_name: manager.name
      };

      let res;
      if (form.id) {
        res = await api(`/api/delay-reports/${form.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });
      } else {
        res = await api("/api/delay-reports", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });
      }

      if (res.ok) {
        // Also attach to the ticket if we have a ticket ID and an attachment!
        if (form.task_incident_id && form.attachment_url) {
          try {
            const selectedTicket = allTickets.find(t => {
              const ticketNo = (t.ticketNumber || t.number || t.ticket_number || t.id || "").toLowerCase();
              return ticketNo === form.task_incident_id.toLowerCase();
            });
            if (selectedTicket) {
              await api.post(`/api/tickets/${selectedTicket.id}/activities`, {
                activity_type: 'work_note',
                visibility_type: 'internal',
                created_by: user?.uid,
                created_by_name: profile?.name || user?.email || "Employee",
                message: `Logged Time Delay Justification: ${form.activities_performed_during_delay}`,
                metadata_json: JSON.stringify({
                  attachments: [
                    {
                      name: form.attachment_name || "Attached File",
                      url: form.attachment_url
                    }
                  ]
                })
              });
            }
          } catch (err) {
            console.error("Failed to post delay report attachment to ticket activities:", err);
          }
        }
        alert(status === "Draft" ? "Delay report saved as Draft!" : "Delay report submitted successfully!");
        setIsDelayModalOpen(false);
        fetchDelayReports();
      } else {
        const err = await res.json();
        alert(`Failed to save delay report: ${err.error || "Server error"}`);
      }
    } catch (e: any) {
      alert(`Error: ${e.message}`);
    }
    setSubmitting(false);
  };

  const viewDelayReport = (report: any) => {
    setSelectedDelay(report);
    setIsViewModalOpen(true);
  };

  const editDelayReport = (report: any) => {
    // Format times for input datetime-local
    const fmtTime = (isoStr: string) => {
      if (!isoStr) return "";
      const d = new Date(isoStr);
      const pad = (n: number) => String(n).padStart(2, "0");
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
    };

    setForm({
      id: report.id,
      task_incident_id: report.task_incident_id || "",
      assigned_time: fmtTime(report.assigned_time),
      expected_completion_time: fmtTime(report.expected_completion_time),
      actual_completion_time: fmtTime(report.actual_completion_time),
      total_delay_duration: report.total_delay_duration || "",
      reason_for_delay: report.reason_for_delay || "Technical Issues",
      activities_performed_during_delay: report.activities_performed_during_delay || "",
      meeting_details: report.meeting_details || "",
      technical_system_issues: report.technical_system_issues || "",
      dependency_approval_wait_time: report.dependency_approval_wait_time ? String(report.dependency_approval_wait_time) : "",
      additional_comments: report.additional_comments || "",
      attachment_url: report.attachment_url || "",
      attachment_name: report.attachment_name || ""
    });
    setIsDelayModalOpen(true);
  };

  const totalHours = timesheets.reduce((s, t) => s + (parseFloat(t.total_hours) || 0), 0);
  const approvedHours = timesheets.filter(t => t.status ==="Approved").reduce((s, t) => s + (parseFloat(t.total_hours) || 0), 0);
  const avgPerWeek = timesheets.length ? totalHours / timesheets.length : 0;

  // Hours by task
  const taskMap: Record<string, number> = {};
  allCards.forEach(c => { 
    const task = c.task ||"Unknown";
    taskMap[task] = (taskMap[task] || 0) + (parseFloat(c.hours_worked) || 0); 
  });
  const taskData = Object.entries(taskMap).sort((a, b) => b[1] - a[1]);
  const maxTaskHours = taskData.length ? taskData[0][1] : 1;

  const STATUS_COLORS: Record<string, string> = {
    Draft:"bg-gray-100 text-gray-700 border-gray-200",
    Submitted:"bg-blue-100 text-blue-700 border-blue-200",
    Approved:"bg-green-100 text-green-700 border-green-200",
    Rejected:"bg-red-100 text-red-700 border-red-200",
  };

  const filteredTickets = (Array.isArray(allTickets) ? allTickets : []).filter(t => {
    const isBreached = t.responseSlaStatus === "Breached" || 
                       t.response_sla_status === "Breached" || 
                       t.resolutionSlaStatus === "Breached" || 
                       t.resolution_sla_status === "Breached";
    if (!isBreached) return false;

    const q = (form.task_incident_id || "").toLowerCase();
    const ticketNo = (t.ticketNumber || t.number || t.ticket_number || t.id || "").toLowerCase();
    const title = (t.title || "").toLowerCase();
    return !q || ticketNo.includes(q) || title.includes(q);
  });

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      <div className="flex items-center justify-between pb-4 border-b border-border">
        <div className="flex items-center gap-3">
          <Link to="/timesheet" className="p-2 hover:bg-muted rounded transition-colors">
            <ArrowLeft className="w-4 h-4" />
          </Link>
          <div>
            <h1 className="text-2xl font-bold text-sn-dark">Timesheet Reports</h1>
            <p className="text-sm text-muted-foreground">Analytics for your logged minutes</p>
          </div>
        </div>
        <button
          onClick={openDelayModal}
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl px-4 py-2.5 transition-all shadow-[0_0_15px_rgba(37,99,235,0.3)] flex items-center gap-2 cursor-pointer"
        >
          <Bot className="w-4 h-4" /> Delay Report
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="w-8 h-8 border-4 border-sn-green border-t-transparent rounded-full animate-spin" />
        </div>
      ) : (
        <>
          {/* Stats */}
          <div className="grid grid-cols-4 gap-4">
            {[
              { label:"Total Minutes", value: totalHours.toFixed(0), sub: `${timesheets.length} weeks`, color:"text-sn-dark" },
              { label:"Weekly Average", value: avgPerWeek.toFixed(0), sub:"mins/week", color:"text-blue-600" },
              { label:"Approved Minutes", value: approvedHours.toFixed(0), sub: `${totalHours > 0 ? ((approvedHours / totalHours) * 100).toFixed(0) : 0}% of total`, color:"text-green-600" },
              { label:"Tasks Used", value: taskData.length, sub:"different tasks", color:"text-purple-600" },
            ].map(s => (
              <div key={s.label} className="bg-card rounded-lg border border-border p-5">
                <div className="text-xs font-bold text-muted-foreground uppercase tracking-wider">{s.label}</div>
                <div className={`text-3xl font-bold mt-2 ${s.color}`}>{s.value}</div>
                <div className="text-sm text-muted-foreground mt-1">{s.sub}</div>
              </div>
            ))}
          </div>

          {/* Minutes by Task */}
          <div className="bg-card rounded-lg border border-border overflow-hidden">
            <div className="p-4 border-b border-border bg-muted/30">
              <h3 className="font-semibold">Minutes by Ticket Type</h3>
            </div>
            <div className="p-4 space-y-3">
              {taskData.length === 0 ? (
                <p className="text-center text-muted-foreground py-8">No data yet.</p>
              ) : taskData.map(([task, hrs]) => (
                <div key={task} className="flex items-center gap-4">
                  <div className="w-40 text-sm font-medium truncate">{task}</div>
                  <div className="flex-grow h-4 bg-muted rounded-full overflow-hidden">
                    <div className="h-full bg-sn-green rounded-full transition-all" style={{ width: `${(hrs / maxTaskHours) * 100}%` }} />
                  </div>
                  <div className="w-20 text-right text-sm font-bold">{hrs.toFixed(0)} mins</div>
                  <div className="w-12 text-right text-xs text-muted-foreground">{totalHours > 0 ? ((hrs / totalHours) * 100).toFixed(0) : 0}%</div>
                </div>
              ))}
            </div>
          </div>

          {/* AI Activity Sessions */}
          {activitySessions.length > 0 && (
            <div className="bg-card rounded-lg border border-border overflow-hidden">
              <div className="p-4 border-b border-border bg-blue-50 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Bot className="w-4 h-4 text-blue-500" />
                  <h3 className="font-semibold text-blue-800">AI Activity Tracker Sessions</h3>
                  <span className="bg-blue-100 text-blue-700 text-xs font-bold px-2 py-0.5 rounded-full">
                    {activitySessions.length}
                  </span>
                </div>
                <Link to="/activity-tracker" className="text-xs text-blue-600 hover:underline font-medium">
                  Open Tracker →
                </Link>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="bg-muted/30 border-b border-border text-xs font-bold uppercase tracking-wide text-muted-foreground">
                      <th className="p-3 text-left">Date</th>
                      <th className="p-3 text-left">Start</th>
                      <th className="p-3 text-left">End</th>
                      <th className="p-3 text-right">Duration</th>
                      <th className="p-3 text-center">Status</th>
                      <th className="p-3 text-left">Summary</th>
                    </tr>
                  </thead>
                  <tbody>
                    {activitySessions.map((s: SafeAny) => {
                      const dur = s.duration || 0;
                      const h = Math.floor(dur / 3600);
                      const m = Math.floor((dur % 3600) / 60);
                      const durStr = dur > 0 ? `${h > 0 ? h + 'h ' : ''}${m}m` : '—';
                      return (
                        <tr key={s.id} className="border-b border-border hover:bg-muted/10">
                          <td className="p-3 text-sm">
                            {s.start_time ? new Date(s.start_time).toLocaleDateString() : '—'}
                          </td>
                          <td className="p-3 text-sm">
                            {s.start_time ? new Date(s.start_time).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }) : '—'}
                          </td>
                          <td className="p-3 text-sm">
                            {s.stop_time ? new Date(s.stop_time).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }) : '—'}
                          </td>
                          <td className="p-3 text-right font-bold text-sm">{durStr}</td>
                          <td className="p-3 text-center">
                            <span className={`px-2 py-0.5 rounded text-xs font-semibold
                              ${s.status === 'completed' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'}`}>
                              {s.status === 'completed' ? 'Completed' : 'Active'}
                            </span>
                          </td>
                          <td className="p-3 text-sm text-muted-foreground max-w-xs truncate">
                            {s.summary || '—'}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
              {/* AI-tracked time cards summary */}
              {(() => {
                const aiCards = allCards.filter((c: SafeAny) => (c.short_description || '').startsWith('[AI Tracked]'));
                const aiMins = aiCards.reduce((s: number, c: SafeAny) => s + (parseFloat(c.hours_worked) || 0), 0);
                if (aiCards.length === 0) return null;
                return (
                  <div className="p-3 bg-blue-50 border-t border-blue-100 flex items-center gap-2">
                    <Zap className="w-3.5 h-3.5 text-blue-500" />
                    <span className="text-xs text-blue-700">
                      AI Tracker auto-logged <strong>{aiMins} mins</strong> across <strong>{aiCards.length}</strong> time entries in your timesheets
                    </span>
                  </div>
                );
              })()}
            </div>
          )}

          {/* Weekly History */}
          <div className="bg-card rounded-lg border border-border overflow-hidden">
            <div className="p-4 border-b border-border bg-muted/30">
              <h3 className="font-semibold">Weekly History</h3>
            </div>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="bg-muted/50 border-b border-border text-xs font-bold uppercase tracking-wide text-muted-foreground">
                    <th className="p-3 text-left">Week</th>
                    <th className="p-3 text-right">Total Minutes</th>
                    <th className="p-3 text-center">Status</th>
                    <th className="p-3 text-left">Submitted</th>
                  </tr>
                </thead>
                <tbody>
                  {timesheets.length === 0 ? (
                    <tr><td colSpan={4} className="p-8 text-center text-muted-foreground">No records yet.</td></tr>
                  ) : timesheets.map(ts => (
                    <tr key={ts.id} className="border-b border-border hover:bg-muted/10">
                      <td className="p-3 text-sm font-medium">{ts.week_start} → {ts.week_end}</td>
                      <td className="p-3 text-right font-bold">{(parseFloat(ts.total_hours) || 0).toFixed(0)} mins</td>
                      <td className="p-3 text-center">
                        <span className={`px-2 py-0.5 rounded text-xs font-semibold ${STATUS_COLORS[ts.status] || STATUS_COLORS.Draft}`}>{ts.status}</span>
                      </td>
                      <td className="p-3 text-sm text-muted-foreground">
                        {ts.submitted_at ? new Date(ts.submitted_at).toLocaleDateString() :"—"}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

        </>
      )}

      {/* SUBMISSION FORM MODAL */}
      {isDelayModalOpen && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[999] p-4 animate-in fade-in duration-200">
          <div className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-2xl w-full max-w-lg overflow-hidden flex flex-col animate-in zoom-in-95 duration-200">
            {/* Header */}
            <div className="px-6 py-4 bg-slate-50 dark:bg-slate-950 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-800 dark:text-white uppercase tracking-wider flex items-center gap-2">
                <Clock className="w-5 h-5 text-blue-500" /> Time Delay Report
              </h2>
              <button onClick={() => setIsDelayModalOpen(false)} className="text-slate-400 hover:text-slate-600 dark:text-text-dim dark:hover:text-white hover:bg-slate-100 dark:hover:bg-white/5 rounded-lg p-1.5 transition-colors cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Form */}
            <form className="p-6 space-y-4 overflow-y-auto max-h-[70vh] custom-scrollbar text-left">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1 relative" ref={ticketDropdownRef}>
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Task / Incident ID *</label>
                  <input
                    required
                    type="text"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    placeholder="e.g. INC38190 or TSK812"
                    value={form.task_incident_id}
                    onChange={e => {
                      setForm({ ...form, task_incident_id: e.target.value });
                      setIsTicketDropdownOpen(true);
                    }}
                    onFocus={() => setIsTicketDropdownOpen(true)}
                  />
                  {isTicketDropdownOpen && filteredTickets.length > 0 && (
                    <div className="absolute left-0 right-0 top-[102%] z-[1000] max-h-48 overflow-y-auto bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-lg p-1 text-left custom-scrollbar">
                      {filteredTickets.map(t => {
                        const ticketId = t.ticketNumber || t.number || t.ticket_number || t.id || "";
                        return (
                          <button
                            key={t.id}
                            type="button"
                            onClick={() => {
                              const assignedTime = formatLocalDateTimeForInput(t.createdAt || t.created_at);
                              const expectedTime = formatLocalDateTimeForInput(t.resolutionDeadline || t.resolution_deadline);
                              setForm({
                                ...form,
                                task_incident_id: ticketId,
                                assigned_time: assignedTime || form.assigned_time,
                                expected_completion_time: expectedTime || form.expected_completion_time
                              });
                              setIsTicketDropdownOpen(false);
                            }}
                            className="w-full text-left px-3 py-2 rounded-lg text-xs hover:bg-slate-100 dark:hover:bg-white/5 transition-colors flex flex-col gap-0.5 border-b border-slate-50 dark:border-slate-800 last:border-0"
                          >
                            <span className="font-bold text-blue-600 dark:text-blue-400">{ticketId}</span>
                            <span className="text-[10px] text-slate-500 dark:text-slate-400 truncate">{t.title}</span>
                          </button>
                        );
                      })}
                    </div>
                  )}
                </div>
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Reason for Delay *</label>
                  <select
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    value={form.reason_for_delay}
                    onChange={e => setForm({ ...form, reason_for_delay: e.target.value })}
                  >
                    {["Technical Issues", "Dependency/Approval Wait", "Scope Creep", "Meeting/Discussions", "Unexpected Complexity", "Other"].map(r => (
                      <option key={r} value={r}>{r}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Assigned Time *</label>
                  <input
                    required
                    type="datetime-local"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    value={form.assigned_time}
                    onChange={e => setForm({ ...form, assigned_time: e.target.value })}
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Expected Completion *</label>
                  <input
                    required
                    type="datetime-local"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    value={form.expected_completion_time}
                    onChange={e => setForm({ ...form, expected_completion_time: e.target.value })}
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Actual Completion *</label>
                  <input
                    required
                    type="datetime-local"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    value={form.actual_completion_time}
                    onChange={e => setForm({ ...form, actual_completion_time: e.target.value })}
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Delay Duration</label>
                  <input
                    disabled
                    type="text"
                    className="w-full bg-slate-50 dark:bg-slate-950/50 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-500 outline-none"
                    value={form.total_delay_duration || "Select completion times to compute"}
                  />
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Activities Performed during Delay *</label>
                <textarea
                  required
                  rows={3}
                  className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500 resize-none"
                  placeholder="Describe your troubleshooting or development steps taken during the delay"
                  value={form.activities_performed_during_delay}
                  onChange={e => setForm({ ...form, activities_performed_during_delay: e.target.value })}
                />
                
                {/* File Attachment Input Option */}
                <div className="mt-1 flex items-center justify-between bg-slate-50 dark:bg-slate-950/45 p-2 rounded-xl border border-slate-200/60 dark:border-slate-800">
                  <div className="flex items-center gap-2 overflow-hidden mr-2">
                    <Paperclip className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                    {form.attachment_name ? (
                      <span className="text-xs text-blue-600 dark:text-cyan-400 font-semibold truncate max-w-[200px]" title={form.attachment_name}>
                        {form.attachment_name}
                      </span>
                    ) : (
                      <span className="text-xs text-slate-400 dark:text-text-dim">No file attached</span>
                    )}
                  </div>
                  <label className="shrink-0 cursor-pointer text-[10px] bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 px-2.5 py-1.5 rounded-lg font-bold hover:bg-slate-300 transition-colors uppercase tracking-wider">
                    {uploading ? "Uploading..." : "Attach File"}
                    <input
                      type="file"
                      className="hidden"
                      onChange={handleFileUpload}
                      disabled={uploading}
                    />
                  </label>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Meeting Details (Optional)</label>
                  <input
                    type="text"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    placeholder="Attendees, discussion items..."
                    value={form.meeting_details}
                    onChange={e => setForm({ ...form, meeting_details: e.target.value })}
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Technical System Issues (Optional)</label>
                  <input
                    type="text"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    placeholder="Server latency, environment down..."
                    value={form.technical_system_issues}
                    onChange={e => setForm({ ...form, technical_system_issues: e.target.value })}
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Dependency wait (Hours)</label>
                  <input
                    type="number"
                    step="0.1"
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    placeholder="e.g. 1.5"
                    value={form.dependency_approval_wait_time}
                    onChange={e => setForm({ ...form, dependency_approval_wait_time: e.target.value })}
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Line Manager</label>
                  <select
                    className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                    value={manager.uid}
                    onChange={handleManagerChange}
                  >
                    {managersList.map((m) => (
                      <option key={m.uid} value={m.uid}>
                        {m.name || m.email} ({m.role.replace('_', ' ')})
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-semibold uppercase text-slate-500 dark:text-text-dim">Additional Comments (Optional)</label>
                <input
                  type="text"
                  className="w-full bg-white dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl p-2.5 text-xs text-slate-800 dark:text-white outline-none focus:ring-1 focus:ring-blue-500"
                  value={form.additional_comments}
                  onChange={e => setForm({ ...form, additional_comments: e.target.value })}
                />
              </div>

              {/* Action Buttons */}
              <div className="pt-4 flex justify-end gap-3 border-t border-slate-200 dark:border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsDelayModalOpen(false)}
                  className="px-4 py-2 border border-slate-200 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-white/5 rounded-xl text-xs font-bold text-slate-700 dark:text-slate-300 transition-colors cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="button"
                  disabled={submitting}
                  onClick={() => handleSaveDelayReport("Draft")}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 rounded-xl text-xs font-bold transition-all cursor-pointer"
                >
                  Save Draft
                </button>
                <button
                  type="button"
                  disabled={submitting}
                  onClick={() => handleSaveDelayReport("Submitted/Pending Review")}
                  className="px-5 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold transition-all shadow-lg shadow-blue-500/20 cursor-pointer"
                >
                  {submitting ? "Submitting..." : "Submit Report"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* VIEW DETAILS MODAL */}
      {isViewModalOpen && selectedDelay && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[999] p-4 animate-in fade-in duration-200">
          <div className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-2xl w-full max-w-lg overflow-hidden flex flex-col animate-in zoom-in-95 duration-200">
            {/* Header */}
            <div className="px-6 py-4 bg-slate-50 dark:bg-slate-950 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
              <h2 className="text-base font-bold text-slate-800 dark:text-white uppercase tracking-wider flex items-center gap-2">
                <Eye className="w-5 h-5 text-blue-500" /> Delay Report Details
              </h2>
              <button onClick={() => setIsViewModalOpen(false)} className="text-slate-400 hover:text-slate-600 dark:text-text-dim dark:hover:text-white hover:bg-slate-100 dark:hover:bg-white/5 rounded-lg p-1.5 transition-colors cursor-pointer">
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Content */}
            <div className="p-6 space-y-4 overflow-y-auto max-h-[70vh] custom-scrollbar text-left text-sm text-slate-800 dark:text-white">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Task / Incident ID</span>
                  <span className="font-semibold text-blue-600">{selectedDelay.task_incident_id}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Report Status</span>
                  <span className={`px-2 py-0.5 rounded text-xs font-semibold ${STATUS_COLORS[selectedDelay.report_status.split("/")[0]] || "bg-gray-100"}`}>
                    {selectedDelay.report_status}
                  </span>
                </div>
              </div>

              {selectedDelay.rejection_reason && (
                <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-3.5">
                  <span className="text-[10px] font-bold uppercase block mb-1">Manager Rejection Comment:</span>
                  <p>{selectedDelay.rejection_reason}</p>
                </div>
              )}

              <div className="grid grid-cols-3 gap-4 bg-slate-50 dark:bg-slate-950/40 p-3 rounded-xl border border-slate-100 dark:border-slate-800">
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Assigned Time</span>
                  <span>{selectedDelay.assigned_time ? new Date(selectedDelay.assigned_time).toLocaleString() : "—"}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Expected</span>
                  <span>{selectedDelay.expected_completion_time ? new Date(selectedDelay.expected_completion_time).toLocaleString() : "—"}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Actual</span>
                  <span>{selectedDelay.actual_completion_time ? new Date(selectedDelay.actual_completion_time).toLocaleString() : "—"}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Total Delay Duration</span>
                  <span className="font-bold text-slate-700 dark:text-slate-300">{selectedDelay.total_delay_duration}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Reason for Delay</span>
                  <span>{selectedDelay.reason_for_delay}</span>
                </div>
              </div>

              <div>
                <span className="text-[10px] font-bold uppercase text-slate-400 block">Activities Performed during Delay</span>
                <p className="bg-slate-50 dark:bg-slate-950/40 p-3 rounded-xl border border-slate-100 dark:border-slate-800 mt-1 whitespace-pre-wrap">{selectedDelay.activities_performed_during_delay}</p>
                
                {selectedDelay.attachment_url && (
                  <div className="mt-2 flex items-center justify-between bg-blue-50/50 dark:bg-[#131b3d]/30 p-2.5 rounded-xl border border-blue-100/50 dark:border-cyan-500/20">
                    <div className="flex items-center gap-2 overflow-hidden">
                      <Paperclip className="w-3.5 h-3.5 text-blue-500 shrink-0" />
                      <span className="text-xs font-semibold truncate text-slate-700 dark:text-cyan-400 max-w-[200px]" title={selectedDelay.attachment_name}>
                        {selectedDelay.attachment_name || "Attached File"}
                      </span>
                    </div>
                    <a
                      href={selectedDelay.attachment_url}
                      target="_blank"
                      rel="noreferrer"
                      className="text-xs text-blue-600 hover:text-blue-700 dark:text-cyan-400 dark:hover:underline font-bold"
                    >
                      View File
                    </a>
                  </div>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Meeting Details</span>
                  <span>{selectedDelay.meeting_details || "—"}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Technical System Issues</span>
                  <span>{selectedDelay.technical_system_issues || "—"}</span>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Dependency Wait (Hours)</span>
                  <span>{selectedDelay.dependency_approval_wait_time != null ? `${selectedDelay.dependency_approval_wait_time}h` : "—"}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Employee Name</span>
                  <span>{selectedDelay.employee_name}</span>
                </div>
                <div>
                  <span className="text-[10px] font-bold uppercase text-slate-400 block">Manager Name</span>
                  <span>{selectedDelay.manager_name}</span>
                </div>
              </div>

              <div>
                <span className="text-[10px] font-bold uppercase text-slate-400 block">Additional Comments</span>
                <p>{selectedDelay.additional_comments || "—"}</p>
              </div>

              {/* Close Button */}
              <div className="pt-4 flex justify-end border-t border-slate-200 dark:border-slate-800">
                <button
                  onClick={() => setIsViewModalOpen(false)}
                  className="px-5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 rounded-xl text-xs font-bold transition-all cursor-pointer"
                >
                  Close Detail
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}


