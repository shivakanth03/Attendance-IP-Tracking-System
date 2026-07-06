# Smart Attendance & Network Verification System
## PROJECT SPECIFICATION DOCUMENT

---

## Project Title
**Smart Attendance & Network Verification System**  
Using QR Code, Spring Boot, React, MySQL, JWT, Java Socket Programming and WebSockets.

---

## Project Goal
Develop a web-based attendance management system for educational institutions.
- Staff members create attendance sessions using QR Codes
- Students log in, scan QR Code, and mark attendance
- Backend verifies the student's device belongs to the organization's approved network
- Every record includes complete network, login, device information, and audit logs
- Follows enterprise software architecture

---

## Tech Stack

### Frontend
- React (Vite)
- React Router DOM
- Axios
- Tailwind CSS
- React Hook Form
- React QR Scanner / html5-qrcode
- Chart.js / Recharts
- Socket.io Client

### Backend
- Java 21
- Spring Boot 3.x
- Spring Security
- JWT Authentication (jjwt)
- Spring Data JPA / Hibernate
- Spring Validation
- Spring WebSocket (STOMP)
- ZXing QR Generator
- Java Socket Programming
- Maven

### Database
- MySQL 8.x

---

## User Roles
1. **Super Admin** - Full system control
2. **Staff (Admin)** - Department/session management
3. **Student (User)** - Attendance marking

---

## Authentication
- JWT Access Token + Refresh Token
- BCrypt Password Encoding
- Remember Me functionality
- Role-Based Authorization
- Session Expiration
- Logout (token blacklist)

---

## Features by Role

### Admin Features
**Dashboard:**
- Today's Attendance (Present / Absent / Percentage)
- Today's Sessions
- Recent Attendance
- Online Users
- Network Status
- Charts & Notifications

**Student Management:**
- Add / Edit / Delete Student
- Import / Export Students (CSV/Excel)
- Upload Student Photo
- Assign Department / Year / Section
- Search & Filter Students

**Department Management:**
- Create / Edit / Delete Department
- Assign Students

**Course Management:**
- Create Course, Assign Faculty, Assign Department

**Subject Management:**
- Full CRUD

**Attendance Session:**
- Create Session (Subject, Department, Year, Section, Date, Time, Expiry)
- Generate QR Code
- Close / Pause / Resume Session

### Student Features
**Dashboard:**
- Attendance Percentage
- Today's Attendance
- Recent Attendance
- Subjects
- Announcements

**Profile:**
- Change Password
- Attendance History
- Notifications
- Forgot Password

---

## QR Code System
- Secure QR containing: Encrypted Session ID + Expiry Time + Random Token
- QR expires automatically
- QR cannot be reused (one-time use)

---

## Network Validation
**Detect:**
- Client IP
- Hostname
- Subnet
- Network ID

**Compare** with allowed network list.

**Reject** attendance if outside network.

**Store:**
- IP Address
- Browser
- Operating System
- Device Type
- Network Status
- Timestamp

---

## Java Socket Programming
- Implement Java Socket Server
- Track connected clients
- Display: IP Address, Host Name, Connection Time, Online/Offline Status

---

## Live Attendance
- WebSocket (STOMP over SockJS)
- Admin dashboard updates instantly
- No page refresh required

---

## Reports
**Generate:**
- Daily / Weekly / Monthly Report
- Department / Student / Faculty / Subject Report

**Export:** PDF, Excel, CSV

---

## Analytics
- Attendance Charts
- Department / Subject / Daily / Monthly Charts

---

## Audit Logs
- Login Logs, Logout Logs
- Attendance Logs
- QR Generation Logs
- IP Logs
- Admin Activity Logs

---

## Notifications
- Attendance Success
- Session Started / Closed
- Announcements

---

## Database Tables
- admins
- students
- departments
- courses
- subjects
- faculty
- attendance
- attendance_sessions
- qr_sessions
- login_logs
- audit_logs
- notifications
- student_photos
- device_logs
- network_logs
- refresh_tokens

---

## API Standards
- RESTful APIs
- Proper HTTP Status Codes
- Global Exception Handling
- Input Validation
- Swagger / OpenAPI Documentation

---

## Security
- Spring Security + JWT
- Role Authorization
- CSRF Protection
- CORS Configuration
- Rate Limiting
- SQL Injection Prevention
- XSS Prevention
- BCrypt Password Encryption
- Input Validation

---

## Project Structure

### Backend (`/backend`)
```
src/main/java/com/attendance/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── mapper/
├── security/
├── socket/
├── websocket/
├── configuration/
├── exception/
└── utility/
```

### Frontend (`/frontend`)
```
src/
├── components/
├── pages/
├── layouts/
├── hooks/
├── services/
├── context/
├── utils/
└── assets/
```

---

## Development Phases

| Phase | Title |
|-------|-------|
| 1 | Project Setup |
| 2 | Database Design |
| 3 | Backend APIs |
| 4 | Authentication |
| 5 | Student Module |
| 6 | Admin Module |
| 7 | QR Module |
| 8 | Attendance Module |
| 9 | Socket Programming |
| 10 | Network Verification |
| 11 | WebSockets |
| 12 | Reports |
| 13 | Frontend |
| 14 | Testing |
| 15 | Deployment |

---

## Code Quality Standards
- SOLID Principles
- Repository + DTO + Builder Patterns
- Global Exception Handling
- Logging (SLF4J / Logback)
- Comments & Documentation
- Reusable Components
