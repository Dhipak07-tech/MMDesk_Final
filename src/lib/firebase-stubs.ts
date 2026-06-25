import { SafeAny } from '@/types';
/**
 * src/lib/firebase-stubs.ts
 *
 * Expose Firebase-compatible interfaces backed by the pure Axios API client.
 * Fully type-safe and free of raw fetch() calls.
 */

import api from "./api";

// ---- Inline SLA delay helpers ----
function parseSlaDelayMeta(raw: SafeAny): SafeAny {
  if (!raw) return null;
  if (typeof raw === "object") return raw;
  try { return JSON.parse(raw); } catch { return null; }
}

function parseSlaDelayLogs(raw: SafeAny): SafeAny[] {
  if (!raw) return [];
  if (Array.isArray(raw)) return raw;
  try { const p = JSON.parse(raw); return Array.isArray(p) ? p : []; } catch { return []; }
}

// ---- Performance: In-memory API response cache ----
const API_CACHE_TTL_MS = 30_000;
const apiCache = new Map<string, { data: SafeAny; timestamp: number }>();

// User maps for resolving names/emails
const userEmailMap = new Map<string, string>();
const userNameMap = new Map<string, string>();
let usersPrefetched = false;

async function prefetchUsers() {
  if (usersPrefetched) return;
  try {
    const res = await api.get("/api/users");
    const dbUsers = res.data;
    dbUsers.forEach((u: SafeAny) => {
      const uid = u.uid || String(u.id);
      if (uid) {
        userEmailMap.set(uid, u.email || "");
        userNameMap.set(uid, u.name || "");
      }
    });
    usersPrefetched = true;
  } catch (err) {
    console.error("[API] Prefetch users failed:", err);
  }
}

function getCachedResponse(cacheKey: string): SafeAny | null {
  const entry = apiCache.get(cacheKey);
  if (entry && Date.now() - entry.timestamp < API_CACHE_TTL_MS) return entry.data;
  apiCache.delete(cacheKey);
  return null;
}

function setCachedResponse(cacheKey: string, data: SafeAny): void {
  apiCache.set(cacheKey, { data, timestamp: Date.now() });
  if (apiCache.size > 50) {
    const oldestKey = apiCache.keys().next().value;
    if (oldestKey) apiCache.delete(oldestKey);
  }
}

// ---- Listener registry for onSnapshot simulation ----
interface FallbackListener {
  queryOrDoc: SafeAny;
  onNext: (snapshot: SafeAny) => void;
  active: () => boolean;
  trigger: () => void;
}
const activeListeners: FallbackListener[] = [];

function notifyListeners(path: string, id?: string) {
  activeListeners.forEach((listener) => {
    if (!listener.active()) return;
    const lDoc = listener.queryOrDoc;
    if (lDoc && lDoc.type === "document") {
      if (lDoc.path === path && (!id || lDoc.id === id)) listener.trigger();
    } else if (lDoc && (lDoc.type === "collection" || lDoc.type === "query")) {
      const collPath = lDoc.type === "query" ? lDoc.collectionRef.path : lDoc.path;
      if (collPath === path) listener.trigger();
    }
  });
}

// ---- Reference Objects ----
export class CollectionReference {
  type = "collection" as const;
  constructor(public db: SafeAny, public path: string) {}
}

export class FallbackQuery {
  type = "query" as const;
  constructor(public collectionRef: CollectionReference, public clauses: SafeAny[] = []) {}
}

export class DocumentReference {
  type = "document" as const;
  constructor(public db: SafeAny, public path: string, public id: string) {}
}

// ---- Ticket field mapper (DB → Frontend camelCase) ----
export function mapDbTicketToFrontend(t: SafeAny): SafeAny {
  if (!t) return null;
  const uid = t.created_by || t.createdBy || "";
  const fallbackEmail =
    t.created_by_name && t.created_by_name.includes("@")
      ? t.created_by_name
      : t.caller && t.caller.includes("@")
        ? t.caller
        : "";
  return {
    id: String(t.id || t.ticket_number || ""),
    number: t.ticket_number || t.number || "",
    caller: t.caller || "",
    callerEmail: t.caller_email || t.callerEmail || "",
    category: t.category || "",
    incidentCategory: t.incident_category || t.incidentCategory || "",
    incident_category: t.incident_category || t.incidentCategory || "",
    subcategory: t.subcategory || "",
    service: t.service || "",
    serviceOffering: t.service_offering || t.serviceOffering || "",
    cmdbItem: t.cmdb_item || t.cmdbItem || "",
    title: t.title || "",
    description: t.description || "",
    status: t.status || "New",
    priority: t.priority || "4 - Low",
    impact: t.impact || "3 - Low",
    urgency: t.urgency || "3 - Low",
    channel: t.channel || "Self-service",
    assignmentGroup: t.assignment_group || t.assignmentGroup || "",
    assignment_group: t.assignment_group || t.assignmentGroup || "",
    assignedTo: t.assigned_to || t.assignedTo || "",
    assigned_to: t.assigned_to || t.assignedTo || "",
    assignedToName: t.assigned_to_name || t.assignedToName || "",
    assigned_to_name: t.assigned_to_name || t.assignedToName || "",
    createdBy: uid,
    created_by: uid,
    createdByName: t.created_by_name || t.createdByName || userNameMap.get(uid) || t.caller || "System",
    created_by_name: t.created_by_name || t.createdByName || userNameMap.get(uid) || t.caller || "System",
    createdByEmail: userEmailMap.get(uid) || fallbackEmail || "",
    created_by_email: userEmailMap.get(uid) || fallbackEmail || "",
    resolvedBy: t.resolved_by || t.resolvedBy || "",
    resolved_by: t.resolved_by || t.resolvedBy || "",
    resolvedByName: t.resolved_by_name || t.resolvedByName || "",
    resolved_by_name: t.resolved_by_name || t.resolvedByName || "",
    resolvedAt: t.resolved_at || t.resolvedAt || null,
    resolved_at: t.resolved_at || t.resolvedAt || null,
    closedBy: t.closed_by || t.closedBy || "",
    closed_by: t.closed_by || t.closedBy || "",
    closedByName: t.closed_by_name || t.closedByName || "",
    closed_by_name: t.closed_by_name || t.closedByName || "",
    closedAt: t.closed_at || t.closedAt || null,
    closed_at: t.closed_at || t.closedAt || null,
    responseDeadline: t.response_deadline || t.responseDeadline || null,
    response_deadline: t.response_deadline || t.responseDeadline || null,
    resolutionDeadline: t.resolution_deadline || t.resolutionDeadline || null,
    resolution_deadline: t.resolution_deadline || t.resolutionDeadline || null,
    responseSlaStatus: t.response_sla_status || t.responseSlaStatus || "Pending",
    response_sla_status: t.response_sla_status || t.responseSlaStatus || "Pending",
    resolutionSlaStatus: t.resolution_sla_status || t.resolutionSlaStatus || "Pending",
    resolution_sla_status: t.resolution_sla_status || t.resolutionSlaStatus || "Pending",
    responseSlaStartTime: t.response_sla_start_time || t.responseSlaStartTime || null,
    response_sla_start_time: t.response_sla_start_time || t.responseSlaStartTime || null,
    resolutionSlaStartTime: t.resolution_sla_start_time || t.resolutionSlaStartTime || null,
    resolution_sla_start_time: t.resolution_sla_start_time || t.resolutionSlaStartTime || null,
    firstResponseAt: t.first_response_at || t.firstResponseAt || null,
    first_response_at: t.first_response_at || t.firstResponseAt || null,
    totalPausedTime: t.total_paused_time ?? t.totalPausedTime ?? 0,
    total_paused_time: t.total_paused_time ?? t.totalPausedTime ?? 0,
    onHoldStart: t.on_hold_start || t.onHoldStart || null,
    on_hold_start: t.on_hold_start || t.onHoldStart || null,
    onHoldReason: t.on_hold_reason || t.onHoldReason || "",
    on_hold_reason: t.on_hold_reason || t.onHoldReason || "",
    points: t.points ?? 0,
    slaDelayMeta: parseSlaDelayMeta(t.sla_delay_meta_json || t.slaDelayMeta),
    slaDelayLogs: parseSlaDelayLogs(t.sla_delay_logs_json || t.slaDelayLogs),
    slaPolicy: t.sla_policy || t.slaPolicy || "Default SLA",
    sla_policy: t.sla_policy || t.slaPolicy || "Default SLA",
    sla_name: t.sla_name || t.slaName || t.slaPolicy || "Default SLA",
    createdAt: t.created_at || t.createdAt || null,
    created_at: t.created_at || t.createdAt || null,
    updatedAt: t.updated_at || t.updatedAt || t.created_at || t.createdAt || null,
    updated_at: t.updated_at || t.updatedAt || t.created_at || t.createdAt || null,
    watchList: t.watch_list || t.watchList || "",
    watch_list: t.watch_list || t.watchList || "",
  };
}

// ---- Core Data Fetcher (with deduplication) ----
async function fetchFallbackData(path: string, queryObj?: SafeAny): Promise<any[]> {
  let cacheKey = path;
  if (queryObj && queryObj.clauses) cacheKey += ":" + JSON.stringify(queryObj.clauses);

  const cached = getCachedResponse(cacheKey);
  if (cached) return cached;

  if (inflightRequests.has(cacheKey)) {
    return inflightRequests.get(cacheKey)!;
  }

  console.log(`[API] Fetching data for path: "${path}"`);

  const fetchPromise = (async (): Promise<any[]> => {
    try {
      let result: SafeAny[] = [];

      if (path.startsWith("tickets")) {
        prefetchUsers().catch(() => {});

        let isOnlyOpenQuery = false;
        let isOnlyResolvedQuery = false;
        if (queryObj && queryObj.clauses) {
          const whereClause = queryObj.clauses.find((c: SafeAny) => c.type === "where" && c.field === "status");
          if (whereClause) {
            const val = whereClause.value;
            const openStatuses = ["New", "Open", "In Progress", "Pending", "Pending Approval", "On Hold", "Waiting for Customer", "Awaiting User", "Awaiting Vendor"];
            const resolvedStatuses = ["Resolved", "Closed", "Canceled"];
            if (Array.isArray(val)) {
              isOnlyOpenQuery = val.every((v) => openStatuses.includes(v));
              isOnlyResolvedQuery = val.every((v) => resolvedStatuses.includes(v));
            } else {
              isOnlyOpenQuery = openStatuses.includes(val);
              isOnlyResolvedQuery = resolvedStatuses.includes(val);
            }
          }
        }

        let url = "/api/tickets/all";
        if (isOnlyResolvedQuery) url = "/api/tickets/resolved";
        else if (isOnlyOpenQuery) url = "/api/tickets/open";

        const res = await api.get(url);
        result = res.data.map(mapDbTicketToFrontend);
      } else if (path === "users") {
        const res = await api.get("/api/users");
        result = res.data.map((u: SafeAny) => {
          const uid = u.uid || String(u.id);
          const email = u.email || "";
          const name = u.name || "";
          if (uid) {
            userEmailMap.set(uid, email);
            userNameMap.set(uid, name);
          }
          return { id: uid, uid, name, email, role: u.role || "user", phone: u.phone || "", passwordHash: u.password_hash || "", restrictedModules: u.restrictedModules || [] };
        });
      } else if (path === "settings_groups") {
        const res = await api.get("/api/settings_groups");
        result = res.data;
      } else if (path === "sla_breaches") {
        let url = "/api/sla-breaches/all";
        if (queryObj && queryObj.clauses) {
          const whereClause = queryObj.clauses.find(
            (c: SafeAny) => c.type === "where" && (c.field === "assigned_user" || c.field === "assignedTo")
          );
          if (whereClause && whereClause.value) url = `/api/sla-breaches/user/${whereClause.value}`;
        }
        const res = await api.get(url);
        result = res.data;
      } else if (path === "sla_policies") {
        const res = await api.get("/api/sla/policies");
        result = res.data;
      } else if (path === "company_feature_permissions") {
        let companyId = "";
        if (queryObj && queryObj.clauses) {
          const whereClause = queryObj.clauses.find(
            (c: SafeAny) => c.type === "where" && (c.field === "companyId" || c.field === "company_id")
          );
          if (whereClause) companyId = whereClause.value;
        }
        if (companyId) {
          const res = await api.get(`/api/feature-permissions?company_id=${companyId}`);
          result = res.data;
        } else {
          result = [];
        }
      } else if (path === "companies") {
        const res = await api.get("/api/companies");
        result = res.data;
      } else if (path.includes("/comments")) {
        const parts = path.split("/");
        const ticketId = parts[1];
        const res = await api.get(`/api/tickets/${ticketId}`);
        result = res.data.comments || [];
      } else if (path === "settings_categories" || path === "settings_subcategories" || path === "settings_service_providers" || path === "settings_group_members") {
        const res = await api.get(`/api/${path}`);
        result = res.data;
      } else {
        try {
          const res = await api.get(`/api/documents/${path}`);
          result = res.data;
        } catch {
          result = [];
        }
      }

      setCachedResponse(cacheKey, result);
      return result;
    } catch (err) {
      console.error(`[API] Error fetching path "${path}":`, err);
      return [];
    } finally {
      inflightRequests.delete(cacheKey);
    }
  })();

  inflightRequests.set(cacheKey, fetchPromise);
  return fetchPromise;
}

// ---- Performance: In-flight request deduplication ----
const inflightRequests = new Map<string, Promise<any>>();

// -------------------------------------------------------
// Firestore-compatible API
// -------------------------------------------------------

export function collection(...args: SafeAny[]): SafeAny {
  const path = typeof args[0] === "string" ? args[0] : args[1];
  return new CollectionReference(args[0], path);
}

export function doc(...args: SafeAny[]): SafeAny {
  let path = "";
  let id = "";
  if (args[0] && (args[0].type === "collection" || args[0] instanceof CollectionReference)) {
    path = args[0].path;
    id = args[1];
  } else {
    path = args[1];
    id = args[2];
  }
  return new DocumentReference(args[0], path, id);
}

export function query(queryRef: SafeAny, ...clauses: SafeAny[]): SafeAny {
  if (queryRef && (queryRef.type === "query" || queryRef.type === "collection")) {
    const collRef = queryRef.type === "query" ? queryRef.collectionRef : queryRef;
    const allClauses = queryRef.type === "query" ? [...queryRef.clauses, ...clauses] : clauses;
    return new FallbackQuery(collRef, allClauses);
  }
  return new FallbackQuery(queryRef, clauses);
}

export function where(field: string, op: string, value: SafeAny): SafeAny {
  return { type: "where", field, op, value };
}

export function orderBy(field: string, direction?: SafeAny): SafeAny {
  return { type: "orderBy", field, direction };
}

export function limit(n: number): SafeAny {
  return { type: "limit", limit: n };
}

export async function getDocs(queryObj: SafeAny): Promise<any> {
  const path = queryObj.type === "query" ? queryObj.collectionRef.path : queryObj.path;
  const dataList = await fetchFallbackData(path, queryObj);

  let filteredData = dataList;
  if (queryObj && queryObj.clauses) {
    for (const clause of queryObj.clauses) {
      if (clause.type === "where") {
        filteredData = filteredData.filter((item: SafeAny) => {
          const itemVal = item[clause.field];
          if (clause.op === "==") return itemVal === clause.value;
          if (clause.op === "in") return Array.isArray(clause.value) && clause.value.includes(itemVal);
          if (clause.op === "array-contains") return Array.isArray(itemVal) && itemVal.includes(clause.value);
          return true;
        });
      }
    }
  }

  const docs = filteredData.map((item) => ({
    id: String(item.id),
    data: () => item,
    exists: () => true,
  }));

  return {
    docs,
    empty: docs.length === 0,
    size: docs.length,
    forEach: (callback: SafeAny) => docs.forEach(callback),
  };
}

export async function getDoc(docRef: SafeAny): Promise<any> {
  let path = docRef.path;
  let id = docRef.id;
  if (path && path.includes("/")) {
    const parts = path.split("/");
    id = parts[parts.length - 1];
    path = parts.slice(0, -1).join("/");
  }
  let data: SafeAny = null;

  try {
    if (path === "settings" && id === "branding") {
      try {
        const res = await api.get("/api/settings/branding");
        data = res.data;
      } catch {}
      if (!data) data = { companyName: "Connect", logoBase64: null, logoType: null };
    } else if (path === "tickets") {
      const res = await api.get(`/api/tickets/${id}`);
      data = mapDbTicketToFrontend(res.data);
    } else if (path === "users") {
      const res = await api.get(`/api/users/${id}`);
      data = res.data;
    } else if (path === "companies") {
      const res = await api.get(`/api/companies/${id}`);
      data = res.data;
    } else {
      try {
        const res = await api.get(`/api/${path}/${id}`);
        data = res.data;
      } catch {}
    }
  } catch (e) {
    console.error("[API] getDoc error:", e);
  }

  return { id, exists: () => data !== null, data: () => data };
}

export async function getDocFromServer(docRef: SafeAny): Promise<any> {
  return getDoc(docRef);
}

export function onSnapshot(
  queryOrDoc: SafeAny,
  onNext: (snapshot: SafeAny) => void,
  onError?: (error: SafeAny) => void
): () => void {
  const isDoc = queryOrDoc && queryOrDoc.type === "document";
  let active = true;
  let timerId: SafeAny = null;

  const runPoll = async () => {
    if (!active) return;
    try {
      if (isDoc) {
        const snap = await getDoc(queryOrDoc);
        if (active) onNext(snap);
      } else {
        const snap = await getDocs(queryOrDoc);
        if (active) onNext(snap);
      }
    } catch (err) {
      console.error("[API] onSnapshot poll error:", err);
      if (active && onError) onError(err);
    }
  };

  runPoll();
  timerId = setInterval(runPoll, 30000);

  const listenerRecord: FallbackListener = {
    queryOrDoc,
    onNext,
    active: () => active,
    trigger: runPoll,
  };
  activeListeners.push(listenerRecord);

  return () => {
    active = false;
    if (timerId) clearInterval(timerId);
    const idx = activeListeners.indexOf(listenerRecord);
    if (idx !== -1) activeListeners.splice(idx, 1);
  };
}

export async function addDoc(collectionRef: SafeAny, data: SafeAny): Promise<any> {
  invalidateApiCache();
  const path = collectionRef.path;
  console.log(`[API] addDoc to "${path}":`, data);

  if (path === "tickets") {
    const res = await api.post("/api/tickets/create", {
      ...data,
      caller: data.caller || "System",
      incidentCategory: data.incidentCategory || data.incident_category,
      createdByName: data.createdByName || data.caller || "System",
      customFields: data.customFields || {},
      slaDelayMeta: data.slaDelayMeta || null,
      slaDelayLogs: data.slaDelayLogs || [],
    });
    return { id: String(res.data.id) };
  }

  const settingsPaths = [
    "settings_categories",
    "settings_subcategories",
    "settings_service_providers",
    "settings_groups",
    "settings_group_members",
    "settings_workflows"
  ];

  if (settingsPaths.includes(path)) {
    const res = await api.post(`/api/${path}`, data);
    return { id: String(res.data.id || Date.now()) };
  }

  if (path === "sla_policies") {
    const res = await api.post("/api/sla/policies", data);
    return { id: String(res.data.id || Date.now()) };
  }

  try {
    const res = await api.post(`/api/documents/${path}`, data);
    return { id: String(res.data.id || Date.now()) };
  } catch {}

  return { id: "local_" + Date.now() };
}

export async function updateDoc(docRef: SafeAny, data: SafeAny): Promise<void> {
  invalidateApiCache();
  let path = docRef.path;
  let id = docRef.id;
  if (path && path.includes("/")) {
    const parts = path.split("/");
    id = parts[parts.length - 1];
    path = parts.slice(0, -1).join("/");
  }
  console.log(`[API] updateDoc on "${path}/${id}":`, data);

  if (path === "tickets") {
    const payload = { ...data };
    if (payload.slaDelayMeta !== undefined) {
      payload.sla_delay_meta_json = payload.slaDelayMeta;
      delete payload.slaDelayMeta;
    }
    if (payload.slaDelayLogs !== undefined) {
      payload.sla_delay_logs_json = payload.slaDelayLogs;
      delete payload.slaDelayLogs;
    }
    await api.put(`/api/tickets/${id}`, payload);
    return;
  }

  const settingsPaths = [
    "settings_categories",
    "settings_subcategories",
    "settings_service_providers",
    "settings_groups",
    "settings_group_members",
    "settings_workflows"
  ];
  if (settingsPaths.includes(path)) {
    await api.put(`/api/${path}/${id}`, data);
    return;
  }

  if (path === "sla_policies") {
    await api.put(`/api/sla/policies/${id}`, data);
    return;
  }

  if (path === "users") {
    try {
      await api.put(`/api/users/${id}`, data);
    } catch (err: SafeAny) {
      const errText = err.response?.data?.error || err.response?.data || err.message;
      throw new Error(errText);
    }
    return;
  }

  if (path === "company_feature_permissions") {
    try {
      await api.post("/api/feature-permissions", data);
    } catch (err: SafeAny) {
      const errText = err.response?.data?.error || err.response?.data || err.message;
      throw new Error(errText);
    }
    return;
  }

  await api.put(`/api/documents/${path}/${id}`, data);
}

export async function setDoc(docRef: SafeAny, data: SafeAny, options?: SafeAny): Promise<void> {
  invalidateApiCache();
  let path = docRef.path;
  let id = docRef.id;
  if (path && path.includes("/")) {
    const parts = path.split("/");
    id = parts[parts.length - 1];
    path = parts.slice(0, -1).join("/");
  }
  console.log(`[API] setDoc on "${path}/${id}":`, data);

  if (path === "users") {
    if (options?.merge && id) {
      await api.put(`/api/users/${id}`, data);
    } else {
      await api.post("/api/users", data);
    }
    return;
  }

  if (path === "company_feature_permissions") {
    await api.post("/api/feature-permissions", data);
    return;
  }

  if (path === "settings_groups") {
    await api.post("/api/settings_groups", { id, ...data });
    return;
  }

  if (path === "settings" && id === "branding") {
    let currentData = { companyName: "Connect", logoBase64: null, logoType: null };
    try {
      const getRes = await api.get("/api/settings/branding");
      currentData = getRes.data;
    } catch {}
    const newData = { ...currentData, ...data };
    await api.post("/api/settings/branding", newData);
    notifyListeners("settings", "branding");
    return;
  }

  if (id) {
    await api.put(`/api/documents/${path}/${id}`, data);
  } else {
    await api.post(`/api/documents/${path}`, { id, ...data });
  }
}

export async function deleteDoc(docRef: SafeAny): Promise<void> {
  invalidateApiCache();
  let path = docRef.path;
  let id = docRef.id;
  if (path && path.includes("/")) {
    const parts = path.split("/");
    id = parts[parts.length - 1];
    path = parts.slice(0, -1).join("/");
  }
  console.log(`[API] deleteDoc on "${path}/${id}"`);

  if (path === "tickets") {
    await api.delete(`/api/tickets/${id}`);
    return;
  }

  const settingsPaths = [
    "settings_categories",
    "settings_subcategories",
    "settings_service_providers",
    "settings_groups",
    "settings_group_members",
    "settings_workflows"
  ];
  if (settingsPaths.includes(path)) {
    await api.delete(`/api/${path}/${id}`);
    return;
  }

  if (path === "sla_policies") {
    await api.delete(`/api/sla/policies/${id}`);
    return;
  }

  if (path === "users") {
    await api.delete(`/api/users/${id}`);
    return;
  }

  try {
    await api.delete(`/api/documents/${path}/${id}`);
  } catch {}
}

export function serverTimestamp(): string {
  return new Date().toISOString();
}

export function increment(n: number) {
  return { type: "increment", value: n };
}

export function writeBatch(db: SafeAny): SafeAny {
  const operations: SafeAny[] = [];
  return {
    set: (docRef: SafeAny, data: SafeAny, options?: SafeAny) => operations.push({ type: "set", docRef, data, options }),
    update: (docRef: SafeAny, data: SafeAny) => operations.push({ type: "update", docRef, data }),
    delete: (docRef: SafeAny) => operations.push({ type: "delete", docRef }),
    commit: async () => {
      for (const op of operations) {
        if (op.type === "set") await setDoc(op.docRef, op.data, op.options);
        else if (op.type === "update") await updateDoc(op.docRef, op.data);
        else if (op.type === "delete") await deleteDoc(op.docRef);
      }
    },
  };
}

export function initializeFirestore(..._args: SafeAny[]): SafeAny {
  return {};
}

// -------------------------------------------------------
// Firestore type stubs (replaces firebase/firestore types)
// -------------------------------------------------------

export class Timestamp {
  constructor(public seconds: number, public nanoseconds: number = 0) {}
  toDate(): Date { return new Date(this.seconds * 1000); }
  toMillis(): number { return this.seconds * 1000; }
  static now(): Timestamp { return new Timestamp(Math.floor(Date.now() / 1000)); }
  static fromDate(date: Date): Timestamp { return new Timestamp(Math.floor(date.getTime() / 1000)); }
  static fromMillis(ms: number): Timestamp { return new Timestamp(Math.floor(ms / 1000)); }
}

export function arrayUnion(...elements: SafeAny[]) {
  return { type: "arrayUnion", elements };
}

export function arrayRemove(...elements: SafeAny[]) {
  return { type: "arrayRemove", elements };
}

// No-op FieldPath stub
export class FieldPath {
  constructor(...segments: string[]) {}
}

export const FieldValue = {
  serverTimestamp: () => new Date().toISOString(),
  increment: (n: number) => ({ type: "increment", value: n }),
  arrayUnion: (...elements: SafeAny[]) => ({ type: "arrayUnion", elements }),
  arrayRemove: (...elements: SafeAny[]) => ({ type: "arrayRemove", elements }),
  delete: () => ({ type: "delete" }),
};

// -------------------------------------------------------
// Expose cache invalidation for external use
// -------------------------------------------------------
export function invalidateApiCache() {
  apiCache.clear();
  usersPrefetched = false;
}

// -------------------------------------------------------
// Firebase Compatibility Stubs (Moved from firebase.ts)
// -------------------------------------------------------
export const firebaseAvailable = false;

export const auth = {
  currentUser: null as any,
  onAuthStateChanged: (_callback: SafeAny) => {
    return () => {};
  },
};

export const db = {} as any;

export enum OperationType {
  CREATE = "create",
  UPDATE = "update",
  DELETE = "delete",
  LIST = "list",
  GET = "get",
  WRITE = "write",
}

export interface FirestoreErrorInfo {
  error: string;
  operationType: OperationType;
  path: string | null;
  authInfo: {
    userId: string | undefined;
    email: string | null | undefined;
    emailVerified: boolean | undefined;
    isAnonymous: boolean | undefined;
    tenantId: string | null | undefined;
    providerInfo: {
      providerId: string;
      displayName: string | null;
      email: string | null;
      photoUrl: string | null;
    }[];
  };
}

export function handleFirestoreError(
  error: unknown,
  operationType: OperationType,
  path: string | null
) {
  console.error("[API Error]", operationType, path, error instanceof Error ? error.message : error);
}

// -------------------------------------------------------
// Firebase Auth & App Compatibility Stubs
// -------------------------------------------------------

export async function createUserWithEmailAndPassword(
  _auth: SafeAny,
  _email: string,
  _password: string
): Promise<any> {
  return {
    user: {
      uid: "local_" + Date.now(),
      email: _email,
      displayName: null,
    },
  };
}

export async function updateProfile(_user: SafeAny, _profile: SafeAny): Promise<void> {}

export function onAuthStateChanged(_auth: SafeAny, _callback: SafeAny): () => void {
  return () => {};
}

export async function signOut(_auth: SafeAny): Promise<void> {}

export async function signInAnonymously(_auth: SafeAny): Promise<any> {
  return { user: { uid: "anon_" + Date.now(), email: null } };
}

export async function signInWithEmailAndPassword(
  _auth: SafeAny,
  _email: string,
  _password: string
): Promise<any> {
  return { user: { uid: "local_" + Date.now(), email: _email } };
}

export function getAuth(_app?: SafeAny): SafeAny {
  return { currentUser: null };
}

export function connectAuthEmulator(_auth: SafeAny, _url: string): void {}

export const GoogleAuthProvider = class {};
export const EmailAuthProvider = class {};

export function initializeApp(_config: SafeAny, _name?: string): SafeAny {
  return {};
}

export function getApp(_name?: string): SafeAny {
  return {};
}

export function getApps(): SafeAny[] {
  return [];
}

export type FirebaseApp = any;
export type FirebaseOptions = any;

