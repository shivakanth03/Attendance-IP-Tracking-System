# Smart Attendance & Network Verification System

## Overview

The **Smart Attendance & Network Verification System** is a secure, enterprise-level web application designed to automate attendance management in educational institutions and organizations. The system combines **QR Code-based attendance**, **IP address verification**, and **Java Socket Programming** to ensure that attendance is marked only by authorized users connected to the organization's network.

The application provides separate dashboards for **Administrators**, **Staff**, and **Students**, enabling efficient management of departments, faculty, students, attendance sessions, reports, and system settings.

This project follows a modern full-stack architecture using **Spring Boot**, **React.js**, **MySQL**, and **JWT Authentication**, making it scalable, secure, and suitable for real-world deployment.

---

# Project Objectives

* Eliminate manual attendance processes.
* Prevent proxy attendance using secure QR codes.
* Verify users are connected to the institution's local network.
* Track client IP addresses for security auditing.
* Provide real-time attendance monitoring.
* Generate attendance reports in multiple formats.
* Implement enterprise-level authentication and authorization.

---

# Key Features

## Authentication & Authorization

* JWT-based Authentication
* Role-Based Access Control (RBAC)
* Secure Password Encryption (BCrypt)
* Login & Logout
* Refresh Token Support
* Password Change
* Profile Management

---

## Admin Module

The administrator has complete control over the system.

Features include:

* Dashboard Analytics
* User Management
* Student Management
* Staff Management
* Department Management
* Subject Management
* Attendance Monitoring
* QR Session Management
* Report Generation
* Network Monitoring
* System Configuration

---

## Staff Module

Staff members can:

* Login securely
* Create attendance sessions
* Generate QR codes
* Manage attendance
* View student attendance
* Download reports
* Monitor attendance sessions

---

## Student Module

Students can:

* Login securely
* Scan attendance QR code
* View attendance history
* View profile
* Receive attendance confirmation
* Track attendance percentage

---

# QR Code Attendance

The system generates a unique encrypted QR Code for every attendance session.

Each QR Code includes:

* Session ID
* Subject ID
* Faculty ID
* Expiry Time
* Encrypted Token

Students scan the QR code using the web application.

The backend validates:

* QR Token
* Session Status
* Expiry Time
* Duplicate Attendance
* Student Authentication

---

# Network Verification

One of the core features of the project is network verification.

The backend captures:

* Client IP Address
* Server IP Address
* Network Prefix
* Subnet Information
* Hostname
* Browser Information
* Device Information

The system verifies whether the student's device belongs to the same organizational network before accepting attendance.

If the client belongs to a different network, attendance is rejected and the event is logged.

---

# Java Socket Programming

Java Socket Programming is used to:

* Detect active client connections
* Identify client IP addresses
* Validate network communication
* Track connected devices
* Monitor socket communication
* Verify network availability

---

# Attendance Workflow

1. Staff creates an attendance session.
2. The system generates a secure QR Code.
3. Students log into the application.
4. Students scan the QR Code.
5. Backend validates:

   * User Authentication
   * QR Token
   * Session Expiry
   * IP Address
   * Network Verification
6. Attendance is recorded.
7. Dashboard updates in real time.

---

# Dashboard

The dashboard provides real-time analytics including:

* Total Students
* Total Staff
* Total Departments
* Total Subjects
* Today's Attendance
* Present Students
* Absent Students
* Network Verification Status
* Attendance Trends
* Department-wise Statistics
* Monthly Reports

---

# Reports

The system can generate:

* Daily Attendance Report
* Weekly Report
* Monthly Report
* Student-wise Report
* Subject-wise Report
* Department-wise Report

Reports can be exported as:

* PDF
* Excel
* CSV

---

# Security Features

* JWT Authentication
* Spring Security
* BCrypt Password Encryption
* Role-Based Authorization
* CORS Configuration
* Input Validation
* Exception Handling
* Audit Logging
* Secure API Access
* Token Validation

---

# Technology Stack

## Backend

* Java 17 / 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Maven
* JWT
* Lombok
* MapStruct
* WebSocket
* Java Socket Programming
* ZXing QR Code Library
* Swagger/OpenAPI

---

## Frontend

* React.js
* Vite
* React Router
* Axios
* Tailwind CSS
* Context API
* React Hook Form
* Chart.js
* React Toastify

---

## Database

* MySQL

---

## Development Tools

* IntelliJ IDEA / VS Code
* Maven
* Git
* GitHub
* Postman
* MySQL Workbench

---

# Project Architecture

```
Frontend (React.js)
        │
        ▼
REST API (Spring Boot)
        │
        ▼
Service Layer
        │
        ▼
Repository Layer
        │
        ▼
MySQL Database
        │
        ▼
Attendance Records

                │
                ▼
Java Socket Programming
                │
                ▼
Network Verification

                │
                ▼
QR Code Attendance
```

---

# Database Modules

* Users
* Roles
* Students
* Faculty
* Departments
* Courses
* Subjects
* Attendance Sessions
* Attendance Records
* QR Tokens
* Network Logs
* Login Logs
* Audit Logs
* Notifications

---

# API Modules

* Authentication API
* User API
* Student API
* Faculty API
* Department API
* Subject API
* Attendance API
* QR Code API
* Dashboard API
* Report API
* Notification API
* Network Verification API

---

# Future Enhancements

* Face Recognition Attendance
* Biometric Authentication
* Mobile Application
* GPS-Based Attendance
* AI-Based Attendance Analytics
* Email Notifications
* SMS Notifications
* Multi-Campus Support
* Cloud Deployment
* Docker & Kubernetes Support

---

# Installation

## Clone the Repository

```bash
git clone https://github.com/your-username/attendance-ip-tracking-system.git
```

## Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend URL:

```
http://localhost:8080
```

---

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```
http://localhost:5173
```

---

## Database

Create a MySQL database:

```sql
CREATE DATABASE smart_attendance;
```

Update the database configuration in:

```
backend/src/main/resources/application.properties
```

Run the backend to automatically create the required tables (if Hibernate auto-DDL is enabled).

---

