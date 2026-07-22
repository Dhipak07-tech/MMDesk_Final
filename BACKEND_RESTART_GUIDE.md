# 🚀 Backend & Tunnel Restart Guide

This document explains what happens when your computer is shut down and provides simple step-by-step instructions to restart your Spring Boot backend and Cloudflare Tunnel.

---

## ❓ What Happens When You Shutdown Your Computer?

1. **Spring Boot Backend**: The local Java server running on `http://127.0.0.1:3000` stops.
2. **Cloudflare Tunnel**: The HTTPS public connection (`.trycloudflare.com`) closes.
3. **Vercel Frontend**: Your frontend app at **[https://mmdesk-frontend.vercel.app](https://mmdesk-frontend.vercel.app)** remains online, but API requests will show Network Errors until the local backend is started again.

---

## 🛠️ Step-by-Step Restart Instructions

Follow these steps after turning on your computer:

### Step 1: Open PowerShell in Project Folder
Navigate to your project directory:
```powershell
cd e:\MMOPZ\MMO_DEMO
```

### Step 2: Start the Spring Boot Backend
Run the backend startup script:
```powershell
powershell -ExecutionPolicy Bypass -File .\run_backend.ps1
```
> ℹ️ *Wait until you see: `Tomcat started on port 3000 (http)`.*

---

### Step 3: Start the Cloudflare Tunnel
Open a **second PowerShell window**, navigate to the project directory, and run:
```powershell
cd e:\MMOPZ\MMO_DEMO
.\cloudflared.exe tunnel --url http://127.0.0.1:3000
```
Look for your live HTTPS URL in the terminal output:
```text
+--------------------------------------------------------------------------------------------+
|  Your quick Tunnel has been created! Visit it at (it may take some time to be reachable):  |
|  https://your-tunnel-name.trycloudflare.com                                                |
+--------------------------------------------------------------------------------------------+
```

---

## 🔄 Step 4: Sync New Tunnel URL with Vercel (If Tunnel URL Changed)

If your Cloudflare Tunnel URL changed:

1. Open `vercel.json` and replace `VITE_API_BASE_URL` with your new URL:
   ```json
   {
     "outputDirectory": "microservices/core-service-springboot/src/main/resources/static",
     "framework": "vite",
     "build": {
       "env": {
         "VITE_API_BASE_URL": "https://your-tunnel-name.trycloudflare.com"
       }
     }
   }
   ```
2. Redeploy to Vercel production:
   ```powershell
   npx vercel --prod --yes
   ```

---

## 🔑 Login Credentials Reference

| User | Email Address | Password | Role |
|---|---|---|---|
| **Your Account** | `dhipaksankar06@gmail.com` | `password123` | User |
| **Admin** | `admin@technosprint.net` | `Password123!` | Admin |
| **Ultra Super Admin** | `arun.g@technosprint.net` | `Poland@01` | Ultra Super Admin |
| **Ultra Super Admin** | `swedhasris@gmail.com` | `123202` | Ultra Super Admin |
| **Support Agent** | `agent@technosprint.net` | `Password123!` | Support Agent |
