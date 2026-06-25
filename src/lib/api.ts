import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  // Inject x-user-uid and x-user-email for controllers that use custom header auth
  try {
    const sessionStr = localStorage.getItem('demo_user');
    if (sessionStr) {
      const session = JSON.parse(sessionStr);
      if (session.uid) config.headers['x-user-uid'] = session.uid;
      if (session.email) config.headers['x-user-email'] = session.email;
    }
  } catch { /* ignore parse errors */ }
  return config;
});

export default api;
