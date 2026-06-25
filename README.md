# Ticklora - ITSM Ticketing System

A premium, production-grade IT Service Management (ITSM) ticketing platform built using a modern, scalable, and balanced architecture.

---

## 🏛️ System Architecture

- **Frontend:** React 18 + TypeScript + Vite + Tailwind CSS + Recharts
- **Backend:** Spring Boot 3.4.2 (Java 17, Spring Data JPA, Spring Security, Hibernate)
- **Database:** MySQL 8.0+
- **AI Integration:** Google Gemini AI (for Kiru chatbot, classification, and sentiment suggestions)
- **Email System:** IMAP/SMTP mail integration for automated notifications

---

## 🏗️ Project Structure

```
Ticklora/
├── src/                               ← React TypeScript frontend only
│   ├── main.tsx
│   ├── App.tsx
│   ├── index.css
│   ├── pages/                         ← unique route pages
│   ├── components/                    ← shared UI & layouts
│   ├── contexts/                      ← global state contexts
│   ├── hooks/                         ← custom hooks
│   ├── types/                         ← TypeScript interfaces
│   └── lib/                           ← API clients & logic engines
├── microservices/
│   └── core-service-springboot/       ← Spring Boot Java backend only
│       ├── pom.xml
│       ├── Dockerfile
│       └── src/                       ← Backend Java sources & properties
├── mysql-schema.sql                   ← MySQL schema
├── package.json                       ← Node.json frontend config
├── vite.config.ts                     ← Vite configuration
├── tsconfig.json                      ← TypeScript rules
├── .env                               ← Environment configurations
├── .gitignore                         ← Git exclusion list
└── README.md                          ← Main documentation
```

---

## 📦 Local Installation & Setup

### Prerequisites
- **Node.js** (v18+)
- **Java Development Kit (JDK)** (v17+)
- **Maven** (v3.8+)
- **MySQL Server** (v8.0+)

### 1. Database Setup
1. Create a MySQL database (e.g. `connectit_db`).
2. Run `mysql-schema.sql` to initialize all ITIL compliant tables (users, tickets, SLA policies, approvals, etc.):
   ```bash
   mysql -u root -p connectit_db < mysql-schema.sql
   ```

### 2. Environment Configuration
Create a `.env` file in the root directory:
```env
# Spring Boot Port/API Base URL
VITE_API_BASE_URL=http://localhost:3000

# Optional Google Gemini Key
GEMINI_API_KEY=your_gemini_api_key_here
```

In the backend `microservices/core-service-springboot/src/main/resources/application.properties`, configure the database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/connectit_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 3. Build & Run
- **Start Backend**:
  Navigate to the Spring Boot backend directory and run:
  ```bash
  cd microservices/core-service-springboot
  mvn spring-boot:run
  ```
- **Start Frontend**:
  In the project root directory, install npm packages and start the Vite dev server:
  ```bash
  npm install
  npm run dev
  ```
  Open the frontend portal in your browser at `http://localhost:5173`.
