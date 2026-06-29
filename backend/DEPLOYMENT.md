# LeadHunter AI: Production Deployment & System Configurations

This document details the configuration, deployment, and testing specifications required to build and scale LeadHunter AI's complete ecosystem.

---

## 1. ECOSYSTEM OVERVIEW
LeadHunter AI comprises:
- **Mobile Client**: Kotlin, Jetpack Compose, Material 3, and Room Database for standalone reactive local performance and queue caching.
- **Backend Service**: FastAPI (Python 3.12), PostgreSQL 15, and JWT-authenticated gateway managing lead sync, campaigns queue, and diagnostics logs.

---

## 2. FASTAPI BACKEND DEPLOYMENT

### Prerequisites
- Docker & Docker Compose
- Python 3.12+ (if deploying manually on bare metal)

### Option A: Standard Deployment via Docker Compose (Recommended)
Orchestrate both the FastAPI server and the secure PostgreSQL instance instantly:

```bash
cd backend
docker-compose up -d --build
```

This commands automatically:
1. Provisions a PostgreSQL alpine container.
2. Initializes the schema structure using `/backend/schema.sql` automatically.
3. Exposes the API endpoint on port `8000` (`http://localhost:8000`).

### Option B: Bare-Metal Setup
1. Configure a PostgreSQL instance and secure a connection string.
2. Set environment configurations:
   ```bash
   export DATABASE_URL="postgresql://user:pass@host:5432/dbname"
   export JWT_SECRET="your-production-secret-token"
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Run via Uvicorn:
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 8000 --workers 4
   ```

---

## 3. ANDROID APP COMPILATION & TESTING

### Local APK Build
1. Input your Gemini API key in the Google AI Studio Secrets Panel or in the app's Settings screen directly to activate the live Gemini AI engine.
2. Compile and package:
   ```bash
   gradle assembleDebug
   ```
   The build pipeline packages the signed runnable APK inside `app/build/outputs/apk/debug/`.

### Run Automated Tests
Execute local JVM tests and verify visual layouts using Roborazzi screenshot assertions:
- Run Standard & Robolectric Unit Tests:
  ```bash
  gradle :app:testDebugUnitTest
  ```
- Verify Screen Visual States (Roborazzi Visual Regression):
  ```bash
  gradle :app:verifyRoborazziDebug
  ```

---

## 4. SECURITY & API HARDENING
- **JWT Lifespans**: Tokens are generated with an expiration limit of 60 minutes.
- **Enforce SSL/TLS**: Enforce `HTTPS` on production reverse proxies (e.g., Nginx, Cloudflare) with HTTP Strict Transport Security (HSTS) headers.
- **Database Backups**: Schedule automated cron-jobs to backup PostgreSQL:
  ```bash
  pg_dump -U leadhunter_admin -d leadhunter_production > backup_$(date +%F).sql
  ```
