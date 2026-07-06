# 🎓 Smart Attendance & Network Verification System

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)
![Tailwind](https://img.shields.io/badge/Tailwind-CSS-38bdf8?style=for-the-badge&logo=tailwindcss)

**Enterprise-grade QR-based attendance system with real-time network validation**

[Features](#features) • [Tech Stack](#tech-stack) • [Quick Start](#quick-start) • [API Docs](#api-documentation) • [Architecture](#architecture)

</div>

---

## 📋 Overview

A production-quality web-based attendance management system designed for educational institutions. Staff members create attendance sessions with encrypted QR Codes. Students scan QR codes to mark attendance, and the system validates whether the student's device is on the approved campus network before recording attendance.

---

## ✨ Features

### 🔐 Authentication & Security
- JWT Access + Refresh Token flow
- BCrypt password encoding
- Role-based authorization (Super Admin / Staff / Student)
- Session expiration & token blacklisting
- Rate limiting, CORS, XSS prevention

### 👨‍💼 Admin Dashboard
- Live attendance statistics (Present / Absent / Percentage)
- Real-time session monitoring via WebSockets
- Online users tracker via Java Socket Server
- Charts & analytics
- Network status monitor

### 🎓 Student Management
- Full CRUD with search, filter, pagination
- Bulk import (CSV/Excel) & export
- Photo upload
- Department / Year / Section assignment

### 📋 Attendance Sessions
- Create sessions with Subject, Department, Year, Section, Date, Time, Expiry
- Generate encrypted one-time QR Codes (AES-256)
- Pause / Resume / Close sessions
- Live attendance feed via WebSocket

### 📱 QR Code System
- AES-256 encrypted QR payload
- Automatic expiry
- One-time use enforcement
- Anti-replay protection

### 🌐 Network Validation
- Detects client IP, hostname, subnet, network ID
- Rejects attendance from outside approved subnet
- Stores device fingerprint (browser, OS, device type)

### 🔌 Java Socket Server
- Tracks connected TCP clients
- Displays IP, hostname, connection time, online status
- Pushes updates to admin dashboard

### 📊 Reports & Analytics
- Daily / Weekly / Monthly reports
- Export to PDF, Excel, CSV
- Department, Subject, Student, Faculty reports
- Interactive charts

### 📝 Audit Logs
- Login / Logout logs
- Attendance logs
- QR generation logs
- Admin activity logs
- IP & network logs

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18 + Vite, Tailwind CSS, React Router |
| State | Context API + React Query |
| Charts | Recharts + Chart.js |
| QR | html5-qrcode, ZXing |
| Backend | Java 21, Spring Boot 3.x |
| Security | Spring Security, JWT (jjwt) |
| ORM | Spring Data JPA / Hibernate |
| WebSocket | STOMP over SockJS |
| Socket | Java Socket Server (TCP) |
| Database | MySQL 8.x |
| Reports | iText 7 (PDF), Apache POI (Excel) |
| Build | Maven (Backend), Vite (Frontend) |
| Docs | Swagger / OpenAPI 3 |

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8.x
- Maven 3.9+

### 1. Clone the repository
```bash
git clone https://github.com/your-org/smart-attendance-network-system.git
cd smart-attendance-network-system
```

### 2. Database setup
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql
```

### 3. Configure backend
Edit `backend/src/main/resources/application-dev.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/attendance_db
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your_jwt_secret_key
app.network.allowed-subnet=192.168.1.0/24
```

### 4. Run backend
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Run frontend
```bash
cd frontend
npm install
npm run dev
```

### 6. Access the application
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## 📁 Project Structure

```
Smart-Attendance-Network-System/
├── backend/          # Spring Boot Java backend
├── frontend/         # React + Vite frontend
├── database/         # SQL schema, seed data, migrations
├── socket-server/    # Standalone Java Socket Server
├── docs/             # Architecture diagrams, SRS
├── scripts/          # Build, start, deploy scripts
├── uploads/          # File uploads (photos, QR codes, reports)
└── logs/             # Application logs
```

---

## 📚 Documentation

- [Project Specification](PROJECT_SPECIFICATION.md)
- [API Documentation](API_DOCUMENTATION.md)
- [Database Design](DATABASE_DESIGN.md)
- [Security Guide](SECURITY_GUIDE.md)
- [Deployment Guide](DEPLOYMENT_GUIDE.md)
- [Development Roadmap](DEVELOPMENT_ROADMAP.md)

---

## 🧱 Architecture

```
┌─────────────────────────────────────────────────┐
│              React Frontend (Vite)              │
│   Dashboard │ Scanner │ Reports │ Admin Panel   │
└────────────────────┬────────────────────────────┘
                     │ HTTP / WebSocket
┌────────────────────▼────────────────────────────┐
│           Spring Boot Backend (Java 21)         │
│  REST APIs │ JWT Auth │ WebSocket │ QR Engine   │
│  Network Validator │ Socket Server │ Scheduler  │
└────────────────────┬────────────────────────────┘
                     │ JPA / Hibernate
┌────────────────────▼────────────────────────────┐
│                  MySQL 8.x                      │
│  16+ Normalized Tables │ Audit Logs │ Indexes   │
└─────────────────────────────────────────────────┘
```

---

## 👥 User Roles

| Role | Access |
|---|---|
| Super Admin | Full system control, all modules |
| Staff (Admin) | Create sessions, manage students, view reports |
| Student | Scan QR, view own attendance, profile |

---

## 🔒 Default Credentials (Dev Only)

| Role | Email | Password |
|---|---|---|
| Super Admin | superadmin@college.edu | Admin@1234 |
| Staff | staff@college.edu | Staff@1234 |
| Student | student@college.edu | Student@1234 |

> ⚠️ Change all credentials before deploying to production.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

<div align="center">
Built with ❤️ for educational institutions
</div>
