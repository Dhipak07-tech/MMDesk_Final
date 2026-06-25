/// <reference types="vite/client" />

declare module '@/lib/api' {
  const api: any;
  export default api;
}

declare module '../lib/api' {
  const api: any;
  export default api;
}
