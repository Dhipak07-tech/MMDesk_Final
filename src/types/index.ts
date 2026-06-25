/**
 * src/types/index.ts
 *
 * Centralized TypeScript types, interfaces, and enums for Ticklora.
 */

export type Role =
  | "user"
  | "agent"
  | "sub_admin"
  | "admin"
  | "super_admin"
  | "ultra_super_admin";

export interface User {
  id: string;
  uid: string;
  email: string;
  name: string;
  role: Role;
  phone?: string;
  department?: string;
  isActive?: boolean;
  isDemo?: boolean;
  restrictedModules?: string[];
}

export interface Ticket {
  id: string;
  number: string;
  title: string;
  status: string;
  priority: string;
  assignedTo?: string;
  assignedToName?: string;
  createdBy: string;
  createdAt: SafeAny;
  updatedAt: SafeAny;
}

export interface SLAPolicy {
  id: string;
  name: string;
  priority: string;
  category?: string;
  responseTimeHours?: number;
  resolutionTimeHours?: number;
  allowPause?: boolean;
  isActive?: boolean;
  description?: string;
}

export interface Comment {
  id: string;
  ticketId: string;
  userId: string;
  userName: string;
  userRole: string;
  message: string;
  isInternal: boolean;
  createdAt: SafeAny;
}

export interface Asset {
  id: string;
  name: string;
  type: string;
  status: string;
  owner?: string;
  ownerName?: string;
  location?: string;
  serialNumber?: string;
  model?: string;
  manufacturer?: string;
  ipAddress?: string;
  description?: string;
}

export type SafeAny = any;

