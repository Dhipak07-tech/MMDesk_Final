import { SafeAny } from '@/types';
import { clsx, type ClassValue } from"clsx"
import { twMerge } from"tailwind-merge"

export function cn(...inputs: ClassValue[]) {
 return twMerge(clsx(inputs))
}

export function formatDate(date: SafeAny): string {
  if (!date) return "";
  try {
    let d: Date;
    if (typeof date === "object" && date.seconds !== undefined) {
      const ms = Number(date.seconds) * 1000;
      if (isNaN(ms)) return "";
      d = new Date(ms);
    } else if (date instanceof Date) {
      d = date;
    } else {
      d = new Date(date);
    }
    if (isNaN(d.getTime())) return "";
    return new Intl.DateTimeFormat("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric"
    }).format(d);
  } catch (e) {
    return "";
  }
}

