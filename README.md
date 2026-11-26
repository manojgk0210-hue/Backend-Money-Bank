# 🏦 FintechBank — Full-Stack Digital Banking Application

A production-ready digital banking application built with **Java Spring Boot 3** and **React 18**.  
Supports JWT auth, multiple accounts, deposits, withdrawals, transfers, and full transaction history.

---

## ✨ Features

| Feature | Details |
|---|---|
| 🔐 Authentication | JWT-based register & login, BCrypt password hashing |
| 💳 Accounts | Savings, Checking, Current — unlimited accounts per user |
| 💰 Transactions | Deposit, Withdraw, Transfer with overdraft protection |
| 📊 Dashboard | Balance overview, activity chart, recent transactions |
| 📋 History | Paginated & searchable full transaction log |
| 📱 Responsive | Works on mobile, tablet, and desktop |

---

## 🗂️ Project Structure

```
banking-app/
│
├── 📄 pom.xml                           Maven dependencies (Spring Boot 3.2.5)
├── 📄 Dockerfile                        Multi-stage Docker build
├── 📄 docker-compose.yml                Full stack local run with MySQL
├── 📄 README.md
│
├── 📁 src/main/java/com/bank/          ── BACKEND ──────────────────────────
│   │
│   ├── BankingApplication.java          Entry point (@SpringBootApplication)
│   │
│   ├── 📁 config/
│   │   └── SecurityConfig.java          Spring Security 6, CORS, JWT filter chain
│   │
│   ├── 📁 security/
│   │   ├── JwtService.java              JJWT 0.12.6 — token build, parse, validate
│   │   └── JwtAuthFilter.java           OncePerRequestFilter — attaches auth to context
│   │
│   ├── 📁 model/
│   │   ├── User.java                    JPA entity + implements UserDetails
│   │   ├── Account.java                 Bank account (number, type, balance, status)
│   │   ├── Transaction.java             Transaction record (type, amount, ref, status)
│   │   └── Role.java                    Enum: CUSTOMER | ADMIN
│   │
│   ├── 📁 dto/
│   │   └── Dto.java                     All request/response records in one file
│   │
│   ├── 📁 repository/
│   │   ├── UserRepository.java          findByEmail, existsByEmail
│   │   ├── AccountRepository.java       findByUser, findByAccountNumber
│   │   └── TransactionRepository.java   findAllByAccount (paginated)
│   │
│   ├── 📁 service/
│   │   ├── AuthService.java             Register (unique email) + login → JWT
│   │   ├── AccountService.java          Create account, list, ownership check
│   │   └── TransactionService.java      Deposit / Withdraw / Transfer logic
│   │
│   ├── 📁 controller/
│   │   ├── AuthController.java          POST /api/auth/register|login
│   │   ├── AccountController.java       GET|POST /api/accounts
│   │   └── TransactionController.java   POST deposit|withdraw|transfer + GET history
│   │
│   └── 📁 exception/
│       ├── BankingException.java        Custom exceptions (NotFound, Insufficient, etc.)
│       └── GlobalExceptionHandler.java  @RestControllerAdvice → structured error JSON
│
├── 📁 src/main/resources/
│   └── application.yml                  DB, JWT, server config (env-variable driven)
│
└── 📁 frontend/                        ── FRONTEND (React 18 + Vite) ──────────
    ├── index.html
    ├── vite.config.js                   Vite 5 + proxy /api → localhost:8080
    ├── package.json
    │
    └── src/
        ├── main.jsx                     React DOM root
        ├── App.jsx                      React Router v6 routes + auth guards
        ├── index.css                    CSS variables, animations, global styles
        │
        ├── 📁 context/
        │   └── AuthContext.jsx          Global auth state (user, login, logout)
        │
        ├── 📁 services/
        │   └── api.js                   Axios + JWT interceptor + all API methods
        │
        ├── 📁 components/
        │   ├── Layout.jsx               Sidebar navigation + topbar + user menu
        │   ├── Layout.module.css
        │   ├── UI.jsx                   Card, Button, Input, Select, Badge,
        │   │                            StatCard, Toast, Skeleton, Empty, PageHeader
        │   └── UI.module.css
        │
        └── 📁 pages/
            ├── LoginPage.jsx            Sign-in form with animated background
            ├── RegisterPage.jsx         Sign-up with 5-field form
            ├── Auth.module.css
            ├── DashboardPage.jsx        Stats + account cards + chart + transactions
            ├── Dashboard.module.css
            ├── AccountsPage.jsx         Account grid + open-new-account modal
            ├── Accounts.module.css
            ├── TransferPage.jsx         Deposit / Withdraw / Transfer in one page
            ├── Transfer.module.css
            ├── TransactionsPage.jsx     Full paginated history with search filter
            └── Transactions.module.css
```

---

## 🔧 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Security | Spring Security | 6.x |
| JWT | JJWT | **0.12.6** |
| ORM | Spring Data JPA + Hibernate | 6.x |
| Database | MySQL | 8.0 |
| Frontend | React | 18.3 |
| Routing | React Router | v6 |
| HTTP | Axios | 1.7 |
| Charts | Recharts | 2.12 |
| Icons | Lucide React | 0.395 |
| Build | Vite | 5.x |

---

## 🚀 Running on Localhost

### Prerequisites — install these first

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17+ | https://adoptium.net |
| MySQL | 8.0+ | https://dev.mysql.com/downloads |
| Node.js | 18+ | https://nodejs.org |
| IntelliJ IDEA | Any | https://jetbrains.com/idea |

---

### Step 1 — Create the MySQL database

Open MySQL command line (`mysql -u root -p`) and run:

```sql
CREATE DATABASE banking_db;
```

> ✅ You only need this one command. Spring Boot auto-creates all tables on startup.

---

### Step 2 — Set your MySQL password

Open `src/main/resources/application.yml` and update:

```yaml
spring:
  datasource:
    password: YOUR_MYSQL_PASSWORD    # ← change this line only
```

---

### Step 3 — Run the backend (IntelliJ IDEA)

1. Open IntelliJ → **File → Open** → select the `banking-app` folder
2. Click **Trust Project**
3. Wait for Maven to download dependencies *(first time ~2 min)*
4. Open `src/main/java/com/bank/BankingApplication.java`
5. Click the green **▶** button next to `main()`
6. Watch for: `Started BankingApplication in X seconds`

Verify it works → open: **http://localhost:8080/actuator/health**  
You should see: `{"status":"UP"}`

---

### Step 4 — Run the frontend

Open a new terminal (IntelliJ: `View → Tool Windows → Terminal`):

```bash
cd frontend
npm install
npm run dev
```

Open your browser at: **http://localhost:5173** 🎉

---

### Step 5 — Test everything

```
1. Register    → http://localhost:5173/register
2. Login       → http://localhost:5173/login
3. Dashboard   → see your balance overview
4. Accounts    → open a Savings account
5. Transfer    → deposit ₹10,000
6. Transfer    → withdraw or transfer to another account
7. Transactions→ see full paginated history
```

---

## 🌐 REST API Reference

All protected endpoints require header: `Authorization: Bearer <token>`

### Auth (public — no token needed)

| Method | URL | Request Body |
|---|---|---|
| POST | `/api/auth/register` | firstName, lastName, email, password, phone |
| POST | `/api/auth/login` | email, password |

**Login response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGci...",
    "tokenType": "Bearer",
    "firstName": "Rahul",
    "email": "rahul@example.com"
  }
}
```

### Accounts 🔐

| Method | URL | Description |
|---|---|---|
| POST | `/api/accounts` | Open new account — body: `{ "accountType": "SAVINGS" }` |
| GET | `/api/accounts` | List all my accounts |
| GET | `/api/accounts/{accountNumber}` | Get single account details |

### Transactions 🔐

| Method | URL | Body |
|---|---|---|
| POST | `/api/transactions/deposit` | accountNumber, amount, description |
| POST | `/api/transactions/withdraw` | accountNumber, amount, description |
| POST | `/api/transactions/transfer` | fromAccountNumber, toAccountNumber, amount |
| GET | `/api/transactions/history/{accountNumber}` | `?page=0&size=20` |

---

## 🚢 Deployment Guide

### Option A — Railway + Vercel *(Free tier, recommended)*

**1. Push to GitHub**
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/banking-app.git
git push -u origin main
```

**2. Deploy Backend → Railway**
1. Go to [railway.app](https://railway.app) → sign in with GitHub
2. New Project → Deploy from GitHub repo → select `banking-app`
3. Add a **MySQL** service from the Railway dashboard
4. In your Spring Boot service, add these **Environment Variables**:

| Variable | Value |
|---|---|
| `DB_URL` | `jdbc:mysql://HOST:PORT/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | Copy from Railway MySQL → `MYSQLUSER` |
| `DB_PASSWORD` | Copy from Railway MySQL → `MYSQLPASSWORD` |
| `JWT_SECRET` | Generate at https://generate-secret.vercel.app/64 |

5. Settings → Networking → **Generate Domain** → copy the URL (e.g. `https://banking-app-xyz.up.railway.app`)

**3. Deploy Frontend → Vercel**
1. Update `frontend/vite.config.js` — change proxy target to your Railway URL:
```js
proxy: {
  '/api': { target: 'https://banking-app-xyz.up.railway.app' }
}
```
2. Go to [vercel.com](https://vercel.com) → New Project → import your GitHub repo
3. Set **Root Directory** to `frontend`
4. Click Deploy — live in ~60 seconds ✅

**4. Fix CORS for production**

In `SecurityConfig.java`, add your Vercel URL:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "https://YOUR-APP.vercel.app"   // ← add this
));
```
Push to GitHub → Railway auto-redeploys.

---

### Option B — Docker Compose *(local or VPS)*

```bash
# Start MySQL + Backend together
docker-compose up --build

# Frontend still runs with npm
cd frontend && npm run dev
```

---

## 📋 Environment Variables Reference

| Variable | Default | Description |
|---|---|---|
| `PORT` | `8080` | Server port |
| `DB_URL` | `jdbc:mysql://localhost:3306/banking_db...` | MySQL JDBC URL |
| `DB_USERNAME` | `root` | MySQL user |
| `DB_PASSWORD` | *(empty)* | MySQL password — **set this!** |
| `JWT_SECRET` | *(dev key)* | Base64 secret — **change in production!** |
| `JWT_EXPIRATION` | `86400000` | Token lifetime in ms (24 hours) |

---

## 🐛 Common Errors & Fixes

| Error | Fix |
|---|---|
| `Access denied for user 'root'` | Wrong password in `application.yml` |
| `Communications link failure` | MySQL not running — start the MySQL service |
| `Port 8080 already in use` | Another app on 8080 — restart IntelliJ or PC |
| `TypeTag::UNKNOWN` | Wrong Java version — set SDK to Java 17 in File → Project Structure |
| `cannot find symbol: claims()` | Old JJWT version — ensure `pom.xml` has `jjwt.version=0.12.6` |
| `CORS error` in browser | Add your frontend URL to `SecurityConfig.java` allowed origins |

