import { createContext, useContext } from "react";

export const TabContext = createContext<{ tabId: string } | null>(null);

export function useCurrentTab() {
  return useContext(TabContext);
}
