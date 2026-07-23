import { SafeAny } from '@/types';
import api from '@/lib/api';
import React, { useState, useEffect } from "react";
import {
  Mail,
  CheckCircle2,
  XCircle,
  Settings,
  Shield,
  Send,
  RefreshCw,
  Lock,
  Eye,
  EyeOff,
  AlertTriangle,
  Server,
  Inbox,
  Check
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "../contexts/AuthContext";

export function EmailIntegrations() {
  const { profile } = useAuth();
  
  // Single Gmail Configuration State
  const [emailAddress] = useState("aakash42633@gmail.com");
  const [smtpHost] = useState("smtp.gmail.com");
  const [smtpPort] = useState(587);
  const [imapHost] = useState("imap.gmail.com");
  const [imapPort] = useState(993);
  
  const [appPassword, setAppPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [updatingPassword, setUpdatingPassword] = useState(false);

  const [testingSmtp, setTestingSmtp] = useState(false);
  const [testingImap, setTestingImap] = useState(false);
  const [sendingTestMail, setSendingTestMail] = useState(false);

  const [smtpResult, setSmtpResult] = useState<{ success: boolean; message: string } | null>(null);
  const [imapResult, setImapResult] = useState<{ success: boolean; message: string } | null>(null);
  const [testMailResult, setTestMailResult] = useState<{ success: boolean; message: string } | null>(null);

  const [logs, setLogs] = useState<SafeAny[]>([]);
  const [loadingLogs, setLoadingLogs] = useState(false);

  // Fetch Email Logs
  const fetchLogs = async () => {
    setLoadingLogs(true);
    try {
      const res = await api.get("/api/email/logs?limit=30");
      if (res.status === 200 && Array.isArray(res.data)) {
        setLogs(res.data);
      }
    } catch (e) {
      console.error("Failed to load email logs:", e);
    } finally {
      setLoadingLogs(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  // 1. Test SMTP Connection
  const handleTestSmtp = async () => {
    setTestingSmtp(true);
    setSmtpResult(null);
    try {
      const res = await api.post("/api/email/smtp-test", {
        password: appPassword || undefined
      });
      setSmtpResult({
        success: res.data?.success ?? true,
        message: res.data?.message || "Gmail SMTP Connection Successful!"
      });
    } catch (e: SafeAny) {
      const errMsg = e.response?.data?.error || e.message || "Gmail SMTP connection failed.";
      setSmtpResult({ success: false, message: errMsg });
    } finally {
      setTestingSmtp(false);
    }
  };

  // 2. Test IMAP Connection
  const handleTestImap = async () => {
    setTestingImap(true);
    setImapResult(null);
    try {
      const res = await api.post("/api/email/imap-test", {
        password: appPassword || undefined
      });
      setImapResult({
        success: res.data?.success ?? true,
        message: res.data?.message || "Gmail IMAP Connection Successful!"
      });
    } catch (e: SafeAny) {
      const errMsg = e.response?.data?.error || e.message || "Gmail IMAP connection failed.";
      setImapResult({ success: false, message: errMsg });
    } finally {
      setTestingImap(false);
    }
  };

  // 3. Send Test Email
  const handleSendTestEmail = async () => {
    setSendingTestMail(true);
    setTestMailResult(null);
    try {
      const res = await api.post("/api/email/send-test", {
        to: emailAddress
      });
      setTestMailResult({
        success: res.data?.success ?? true,
        message: res.data?.message || `Test email dispatched to ${emailAddress}`
      });
      fetchLogs();
    } catch (e: SafeAny) {
      const errMsg = e.response?.data?.error || e.message || "Failed to dispatch test email.";
      setTestMailResult({ success: false, message: errMsg });
    } finally {
      setSendingTestMail(false);
    }
  };

  // 4. Update App Password
  const handleUpdatePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!appPassword.trim()) return;
    setUpdatingPassword(true);
    try {
      const res = await api.post("/api/email/smtp-update", {
        password: appPassword.trim()
      });
      setSmtpResult({
        success: res.data?.success ?? true,
        message: res.data?.message || "Gmail App Password updated and verified!"
      });
      setAppPassword("");
    } catch (e: SafeAny) {
      const errMsg = e.response?.data?.error || e.message || "Failed to update App Password.";
      setSmtpResult({ success: false, message: errMsg });
    } finally {
      setUpdatingPassword(false);
    }
  };

  return (
    <div className="standard-page-layout space-y-6">
      {/* Header */}
      <div className="standard-page-header flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-border/40 pb-4">
        <div>
          <h1 className="page-title flex items-center gap-2 text-xl font-bold text-foreground">
            <Mail className="w-6 h-6 text-blue-500" /> Email Integration
          </h1>
          <p className="page-description text-xs text-muted-foreground mt-1">
            Gmail SMTP & IMAP Service Configuration (<span className="font-semibold text-foreground">{emailAddress}</span>)
          </p>
        </div>
        <div className="flex items-center gap-2">
          <span className="px-3 py-1 bg-emerald-500/10 border border-emerald-500/30 text-emerald-500 text-xs font-bold rounded-full flex items-center gap-1.5">
            <CheckCircle2 className="w-3.5 h-3.5" /> Active & Default
          </span>
        </div>
      </div>

      {/* Main Gmail Config Card */}
      <div className="bg-card border border-border rounded-2xl p-6 shadow-sm space-y-6">
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4 border-b border-border/40 pb-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-blue-500/10 text-blue-500 rounded-xl border border-blue-500/20">
              <Mail className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-base font-bold text-foreground">Gmail Service Integration</h2>
              <p className="text-xs text-muted-foreground">Primary email account for outbound notifications & inbound IMAP ticket sync</p>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleTestSmtp}
              disabled={testingSmtp}
              className="h-8 text-xs font-semibold"
            >
              <Server className="w-3.5 h-3.5 mr-1.5 text-blue-500" />
              {testingSmtp ? "Testing SMTP..." : "Test SMTP Connection"}
            </Button>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleTestImap}
              disabled={testingImap}
              className="h-8 text-xs font-semibold"
            >
              <Inbox className="w-3.5 h-3.5 mr-1.5 text-purple-500" />
              {testingImap ? "Testing IMAP..." : "Test IMAP Connection"}
            </Button>
            <Button
              type="button"
              size="sm"
              onClick={handleSendTestEmail}
              disabled={sendingTestMail}
              className="h-8 text-xs font-semibold bg-blue-600 hover:bg-blue-700 text-white"
            >
              <Send className="w-3.5 h-3.5 mr-1.5" />
              {sendingTestMail ? "Sending..." : "Send Test Email"}
            </Button>
          </div>
        </div>

        {/* Configuration Details Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* SMTP Settings */}
          <div className="p-4 rounded-xl bg-muted/20 border border-border/60 space-y-4">
            <div className="flex items-center justify-between border-b border-border/40 pb-2">
              <span className="text-xs font-bold uppercase tracking-wider text-muted-foreground flex items-center gap-1.5">
                <Server className="w-4 h-4 text-blue-500" /> Outbound SMTP Config
              </span>
              <span className="text-[10px] font-semibold text-emerald-500 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
                STARTTLS / Port 587
              </span>
            </div>
            <div className="space-y-3 text-xs">
              <div>
                <label className="text-muted-foreground font-medium block mb-1">Email Account</label>
                <input
                  type="text"
                  readOnly
                  value={emailAddress}
                  className="w-full p-2 border border-border rounded-lg bg-card text-foreground font-mono font-semibold"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-muted-foreground font-medium block mb-1">SMTP Host</label>
                  <input
                    type="text"
                    readOnly
                    value={smtpHost}
                    className="w-full p-2 border border-border rounded-lg bg-card text-foreground font-mono"
                  />
                </div>
                <div>
                  <label className="text-muted-foreground font-medium block mb-1">SMTP Port</label>
                  <input
                    type="text"
                    readOnly
                    value={smtpPort}
                    className="w-full p-2 border border-border rounded-lg bg-card text-foreground font-mono"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* IMAP Settings */}
          <div className="p-4 rounded-xl bg-muted/20 border border-border/60 space-y-4">
            <div className="flex items-center justify-between border-b border-border/40 pb-2">
              <span className="text-xs font-bold uppercase tracking-wider text-muted-foreground flex items-center gap-1.5">
                <Inbox className="w-4 h-4 text-purple-500" /> Inbound IMAP Config
              </span>
              <span className="text-[10px] font-semibold text-purple-500 bg-purple-500/10 px-2 py-0.5 rounded border border-purple-500/20">
                SSL / Port 993
              </span>
            </div>
            <div className="space-y-3 text-xs">
              <div>
                <label className="text-muted-foreground font-medium block mb-1">IMAP Account</label>
                <input
                  type="text"
                  readOnly
                  value={emailAddress}
                  className="w-full p-2 border border-border rounded-lg bg-card text-foreground font-mono font-semibold"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-muted-foreground font-medium block mb-1">IMAP Host</label>
                  <input
                    type="text"
                    readOnly
                    value={imapHost}
                    className="w-full p-2 border border-border rounded-lg bg-card text-foreground font-mono"
                  />
                </div>
                <div>
                  <label className="text-muted-foreground font-medium block mb-1">IMAP Port</label>
                  <input
                    type="text"
                    readOnly
                    value={imapPort}
                    className="w-full p-2 border border-border rounded-lg bg-card text-foreground font-mono"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Update App Password Form */}
        <form onSubmit={handleUpdatePassword} className="p-4 rounded-xl bg-muted/30 border border-border space-y-3">
          <div className="flex items-center justify-between">
            <label className="text-xs font-bold text-foreground flex items-center gap-1.5">
              <Lock className="w-4 h-4 text-amber-500" /> Update Gmail App Password
            </label>
            <span className="text-[10px] text-muted-foreground">Stored securely in environment configuration</span>
          </div>
          <div className="flex gap-2">
            <div className="relative flex-1">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Enter 16-character Gmail App Password..."
                value={appPassword}
                onChange={(e) => setAppPassword(e.target.value)}
                className="w-full p-2 pr-9 border border-border rounded-lg text-xs bg-card text-foreground font-mono"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-2.5 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
            <Button
              type="submit"
              size="sm"
              disabled={updatingPassword || !appPassword.trim()}
              className="h-8 text-xs font-semibold bg-emerald-600 hover:bg-emerald-700 text-white"
            >
              {updatingPassword ? "Verifying..." : "Update Password"}
            </Button>
          </div>
        </form>

        {/* Diagnostic Results Feedback Banners */}
        {smtpResult && (
          <div className={`p-3 rounded-xl border text-xs flex items-center justify-between gap-2 ${
            smtpResult.success ? "bg-emerald-500/10 border-emerald-500/30 text-emerald-500" : "bg-red-500/10 border-red-500/30 text-red-500"
          }`}>
            <div className="flex items-center gap-2">
              {smtpResult.success ? <CheckCircle2 className="w-4 h-4 shrink-0" /> : <XCircle className="w-4 h-4 shrink-0" />}
              <span className="font-semibold">{smtpResult.message}</span>
            </div>
            <button onClick={() => setSmtpResult(null)} className="text-muted-foreground hover:text-foreground">
              <XCircle className="w-4 h-4" />
            </button>
          </div>
        )}

        {imapResult && (
          <div className={`p-3 rounded-xl border text-xs flex items-center justify-between gap-2 ${
            imapResult.success ? "bg-purple-500/10 border-purple-500/30 text-purple-500" : "bg-red-500/10 border-red-500/30 text-red-500"
          }`}>
            <div className="flex items-center gap-2">
              {imapResult.success ? <CheckCircle2 className="w-4 h-4 shrink-0" /> : <XCircle className="w-4 h-4 shrink-0" />}
              <span className="font-semibold">{imapResult.message}</span>
            </div>
            <button onClick={() => setImapResult(null)} className="text-muted-foreground hover:text-foreground">
              <XCircle className="w-4 h-4" />
            </button>
          </div>
        )}

        {testMailResult && (
          <div className={`p-3 rounded-xl border text-xs flex items-center justify-between gap-2 ${
            testMailResult.success ? "bg-blue-500/10 border-blue-500/30 text-blue-500" : "bg-red-500/10 border-red-500/30 text-red-500"
          }`}>
            <div className="flex items-center gap-2">
              {testMailResult.success ? <CheckCircle2 className="w-4 h-4 shrink-0" /> : <XCircle className="w-4 h-4 shrink-0" />}
              <span className="font-semibold">{testMailResult.message}</span>
            </div>
            <button onClick={() => setTestMailResult(null)} className="text-muted-foreground hover:text-foreground">
              <XCircle className="w-4 h-4" />
            </button>
          </div>
        )}
      </div>

      {/* Email Activity & Queue Logs Table */}
      <div className="bg-card border border-border rounded-2xl p-6 shadow-sm space-y-4">
        <div className="flex items-center justify-between border-b border-border/40 pb-3">
          <div>
            <h3 className="text-sm font-bold text-foreground flex items-center gap-2">
              <Shield className="w-4 h-4 text-blue-500" /> Email Activity Logs ({emailAddress})
            </h3>
            <p className="text-xs text-muted-foreground">Recent outbound notifications and diagnostic dispatches</p>
          </div>
          <Button
            type="button"
            variant="ghost"
            size="sm"
            onClick={fetchLogs}
            disabled={loadingLogs}
            className="h-8 text-xs font-semibold"
          >
            <RefreshCw className={`w-3.5 h-3.5 mr-1.5 ${loadingLogs ? "animate-spin" : ""}`} />
            Refresh Logs
          </Button>
        </div>

        <div className="overflow-x-auto border border-border/60 rounded-xl">
          <table className="w-full text-xs text-left">
            <thead className="bg-muted/40 text-muted-foreground font-semibold uppercase tracking-wider text-[10px]">
              <tr>
                <th className="p-3">Status</th>
                <th className="p-3">Recipient</th>
                <th className="p-3">Subject</th>
                <th className="p-3">Created Date</th>
                <th className="p-3">Details / Error</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/40 font-mono">
              {logs.length === 0 ? (
                <tr>
                  <td colSpan={5} className="p-6 text-center text-muted-foreground italic font-sans">
                    No email activity logs recorded yet.
                  </td>
                </tr>
              ) : (
                logs.map((logItem, idx) => (
                  <tr key={logItem.id || idx} className="hover:bg-muted/20 transition-colors">
                    <td className="p-3 font-sans">
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase ${
                        logItem.status === 'sent' || logItem.status === 'completed'
                          ? "bg-emerald-500/10 text-emerald-500 border border-emerald-500/20"
                          : logItem.status === 'failed'
                          ? "bg-red-500/10 text-red-500 border border-red-500/20"
                          : "bg-amber-500/10 text-amber-500 border border-amber-500/20"
                      }`}>
                        {logItem.status || 'sent'}
                      </span>
                    </td>
                    <td className="p-3 text-foreground">{logItem.toEmail || logItem.recipient || emailAddress}</td>
                    <td className="p-3 text-foreground font-sans">{logItem.subject || logItem.event_type || 'Email Notification'}</td>
                    <td className="p-3 text-muted-foreground">{logItem.createdAt ? new Date(logItem.createdAt).toLocaleString() : 'Just now'}</td>
                    <td className="p-3 text-muted-foreground font-sans max-w-xs truncate">
                      {logItem.errorMessage || logItem.error || "Sent successfully"}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
