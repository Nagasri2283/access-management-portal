# Enterprise Workflow & Access Management System

A full-stack portal where employees request access to internal resources, managers/admins approve or reject those requests, and admins manage the resource catalog — with JWT auth and 3 distinct roles.

**Stack:** Spring Boot 3 (Java 17) + PostgreSQL + JWT · React 18 + Vite

---

## 1. Prerequisites

- Java 17 (`java -version`)
- Maven (`mvn -version`)
- Node.js 18+ and npm (`node -v`)
- PostgreSQL running locally

---

## 2. Backend setup

1. **Create the database**
   ```bash
   psql -U postgres
   CREATE DATABASE access_portal;
   \q
   ```

2. **Configure `backend/src/main/resources/application.properties`**
   Update `spring.datasource.username` / `spring.datasource.password` to match your local PostgreSQL. Optionally change `app.jwt.secret` to your own random 32+ character string.

3. **Run it**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Runs on `http://localhost:8080`. Hibernate auto-creates the tables, and a `DataSeeder` automatically inserts 3 demo accounts and 4 demo resources the first time it starts (see below).

4. **Run backend tests**
   ```bash
   mvn test
   ```

### Demo accounts (seeded automatically on first run)

| Role     | Email                  | Password    |
|----------|------------------------|-------------|
| Admin    | admin@company.com      | admin123    |
| Manager  | manager@company.com    | manager123  |
| Employee | employee@company.com   | employee123 |

---

## 3. Frontend setup

1. ```bash
   cd frontend
   npm install
   ```

2. Copy the env file:
   ```bash
   cp .env.example .env
   ```
   (Defaults to `http://localhost:8080`, matching the backend above.)

3. Run it:
   ```bash
   npm run dev
   ```
   Opens on `http://localhost:5173`.

---

## 4. Demo script (what to show/record for your resume/portfolio)

1. Log in as **employee@company.com** → submit a new access request for a resource → see it appear as `PENDING` in your history.
2. Log in as **manager@company.com** → see the request in the Pending queue → approve or reject it with an optional comment.
3. Log back in as the employee → see the request's status has updated with the reviewer's name and comment.
4. Log in as **admin@company.com** → add a new resource to the catalog, view all requests system-wide.

This flow demonstrates: authentication, authorization by role, a full request lifecycle, and CRUD — the core of what an internship reviewer is scanning for in 30 seconds.

---

## 5. Architecture notes (for your README/portfolio writeup)

- **Auth**: stateless JWT (`io.jsonwebtoken`), issued on login/register, validated on every request via a custom `OncePerRequestFilter`.
- **Authorization**: two layers — path-level rules in `SecurityConfig` for admin-only resource management, and method-level `@PreAuthorize` on controller methods for the manager/admin review actions (approve, reject, view-all).
- **Data model**: `User` (role: EMPLOYEE/MANAGER/ADMIN) → `AccessRequest` (status: PENDING/APPROVED/REJECTED/CANCELLED) → `Resource`. Requests reference both the requester and (once reviewed) the reviewer.
- **Frontend**: role-aware routing (`ProtectedRoute`) redirects each role to its own dashboard and blocks cross-role access; a JWT is attached to every API call via an Axios interceptor and cleared automatically on a 401.

---

## 6. API reference

| Method | Endpoint                       | Role              | Description                          |
|--------|---------------------------------|--------------------|--------------------------------------|
| POST   | `/api/auth/register`            | public              | Create an EMPLOYEE account           |
| POST   | `/api/auth/login`                | public              | Log in, returns JWT                  |
| GET    | `/api/resources`                 | any authenticated   | List the resource catalog            |
| POST   | `/api/admin/resources`           | ADMIN               | Add a resource                       |
| DELETE | `/api/admin/resources/{id}`      | ADMIN               | Remove a resource                    |
| POST   | `/api/requests`                  | any authenticated   | Submit an access request             |
| GET    | `/api/requests/my`               | any authenticated   | View your own requests               |
| PUT    | `/api/requests/{id}/cancel`      | owner only          | Cancel your own pending request      |
| GET    | `/api/requests?status=PENDING`   | MANAGER, ADMIN       | View requests (optional status filter)|
| PUT    | `/api/requests/{id}/approve`     | MANAGER, ADMIN       | Approve a pending request            |
| PUT    | `/api/requests/{id}/reject`      | MANAGER, ADMIN       | Reject a pending request             |

---

## 7. Suggested resume bullet

> Built a full-stack RBAC access-management platform (Spring Boot, Spring Security/JWT, PostgreSQL, React) supporting 3 user roles and a complete request-submission-to-approval workflow with audit trail (reviewer, timestamp, comment) and unit-tested service layer.

## 8. Optional next steps (if you have extra time before applying)

- Deploy backend (Render/Railway) + frontend (Vercel/Netlify) for a live demo link
- Add pagination/search on the requests table
- Add a few more `@SpringBootTest` integration tests for the request approval flow
- Record a 60–90 second screen-capture GIF of the demo script above for your GitHub README
