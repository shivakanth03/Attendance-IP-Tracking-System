-- ==============================================================
-- Smart Attendance & Network Verification System
-- Database Schema - MySQL 8.x
-- ==============================================================

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

-- ==============================================================
-- Database Creation
-- ==============================================================
CREATE DATABASE IF NOT EXISTS attendance_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE attendance_db;

-- ==============================================================
-- Table: users (Unified auth for all roles)
-- ==============================================================
CREATE TABLE IF NOT EXISTS users (
    id                    BIGINT          NOT NULL AUTO_INCREMENT,
    email                 VARCHAR(100)    NOT NULL,
    password              VARCHAR(255)    NOT NULL,
    full_name             VARCHAR(100)    NOT NULL,
    phone                 VARCHAR(20),
    role                  ENUM('SUPER_ADMIN','ADMIN','STUDENT') NOT NULL DEFAULT 'STUDENT',
    active                TINYINT(1)      NOT NULL DEFAULT 1,
    locked                TINYINT(1)      NOT NULL DEFAULT 0,
    profile_image         VARCHAR(500),
    last_login            DATETIME,
    password_reset_token  VARCHAR(255),
    password_reset_expiry DATETIME,
    failed_login_attempts INT             NOT NULL DEFAULT 0,
    created_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by            VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    INDEX idx_users_role (role),
    INDEX idx_users_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: departments
-- ==============================================================
CREATE TABLE IF NOT EXISTS departments (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    name                VARCHAR(100)    NOT NULL,
    code                VARCHAR(10),
    description         VARCHAR(500),
    head_of_department  VARCHAR(100),
    active              TINYINT(1)      NOT NULL DEFAULT 1,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_dept_name (name),
    UNIQUE KEY uq_dept_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: courses
-- ==============================================================
CREATE TABLE IF NOT EXISTS courses (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(20)     UNIQUE,
    department_id   BIGINT,
    duration_years  INT,
    description     VARCHAR(500),
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_course_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: faculty
-- ==============================================================
CREATE TABLE IF NOT EXISTS faculty (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    user_id           BIGINT          NOT NULL UNIQUE,
    employee_id       VARCHAR(20)     UNIQUE,
    department_id     BIGINT,
    designation       VARCHAR(100),
    qualification     VARCHAR(100),
    experience_years  INT,
    active            TINYINT(1)      NOT NULL DEFAULT 1,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_faculty_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_faculty_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: subjects
-- ==============================================================
CREATE TABLE IF NOT EXISTS subjects (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100)    NOT NULL,
    code          VARCHAR(20)     UNIQUE,
    department_id BIGINT,
    faculty_id    BIGINT,
    year_of_study VARCHAR(10),
    semester      VARCHAR(10),
    credit_hours  INT,
    description   VARCHAR(500),
    active        TINYINT(1)      NOT NULL DEFAULT 1,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_subject_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT fk_subject_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: students
-- ==============================================================
CREATE TABLE IF NOT EXISTS students (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL UNIQUE,
    roll_number     VARCHAR(20)     NOT NULL UNIQUE,
    register_number VARCHAR(30)     UNIQUE,
    department_id   BIGINT,
    year_of_study   VARCHAR(10),
    section         VARCHAR(10),
    semester        VARCHAR(10),
    admission_year  INT,
    date_of_birth   DATE,
    gender          VARCHAR(10),
    address         VARCHAR(500),
    parent_name     VARCHAR(100),
    parent_phone    VARCHAR(20),
    active          TINYINT(1)      NOT NULL DEFAULT 1,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_student_roll (roll_number),
    INDEX idx_student_dept (department_id),
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: student_photos
-- ==============================================================
CREATE TABLE IF NOT EXISTS student_photos (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    student_id  BIGINT          NOT NULL,
    file_path   VARCHAR(500)    NOT NULL,
    file_name   VARCHAR(255),
    file_size   BIGINT,
    is_primary  TINYINT(1)      NOT NULL DEFAULT 0,
    uploaded_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_photo_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: attendance_sessions
-- ==============================================================
CREATE TABLE IF NOT EXISTS attendance_sessions (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    subject_id    BIGINT          NOT NULL,
    department_id BIGINT          NOT NULL,
    created_by_id BIGINT          NOT NULL,
    year_of_study VARCHAR(10),
    section       VARCHAR(10),
    session_date  DATE            NOT NULL,
    start_time    TIME            NOT NULL,
    end_time      TIME,
    expiry_minutes INT            NOT NULL DEFAULT 5,
    qr_expires_at DATETIME,
    status        ENUM('ACTIVE','PAUSED','CLOSED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    session_token VARCHAR(64)     UNIQUE,
    qr_image_path VARCHAR(500),
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_session_date (session_date),
    INDEX idx_session_status (status),
    INDEX idx_session_dept (department_id),
    CONSTRAINT fk_session_subject FOREIGN KEY (subject_id) REFERENCES subjects(id),
    CONSTRAINT fk_session_dept FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_session_creator FOREIGN KEY (created_by_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: qr_tokens (one-time use QR tokens)
-- ==============================================================
CREATE TABLE IF NOT EXISTS qr_tokens (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    session_id  BIGINT          NOT NULL,
    token       VARCHAR(64)     NOT NULL UNIQUE,
    used        TINYINT(1)      NOT NULL DEFAULT 0,
    used_by_id  BIGINT,
    used_at     DATETIME,
    expires_at  DATETIME        NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_qr_token (token),
    INDEX idx_qr_session (session_id),
    CONSTRAINT fk_qr_session FOREIGN KEY (session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_qr_used_by FOREIGN KEY (used_by_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: attendance
-- ==============================================================
CREATE TABLE IF NOT EXISTS attendance (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    student_id      BIGINT          NOT NULL,
    session_id      BIGINT          NOT NULL,
    status          ENUM('PRESENT','ABSENT','LATE','EXCUSED') NOT NULL DEFAULT 'PRESENT',
    ip_address      VARCHAR(50),
    device_info     VARCHAR(255),
    browser         VARCHAR(100),
    operating_system VARCHAR(100),
    network_status  VARCHAR(50),
    latitude        DECIMAL(10,8),
    longitude       DECIMAL(11,8),
    marked_at       DATETIME,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_attendance_student_session (student_id, session_id),
    INDEX idx_attendance_session (session_id),
    INDEX idx_attendance_student (student_id),
    INDEX idx_attendance_date (marked_at),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES attendance_sessions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: login_logs
-- ==============================================================
CREATE TABLE IF NOT EXISTS login_logs (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_id     BIGINT,
    email       VARCHAR(100),
    action      ENUM('LOGIN','LOGOUT','LOGIN_FAILED') NOT NULL,
    ip_address  VARCHAR(50),
    user_agent  VARCHAR(500),
    success     TINYINT(1)      NOT NULL DEFAULT 1,
    failure_reason VARCHAR(255),
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_login_user (user_id),
    INDEX idx_login_created (created_at),
    CONSTRAINT fk_login_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: audit_logs
-- ==============================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    user_id      BIGINT,
    action       VARCHAR(50)     NOT NULL,
    entity_type  VARCHAR(50),
    entity_id    BIGINT,
    description  VARCHAR(500),
    ip_address   VARCHAR(50),
    user_agent   VARCHAR(255),
    old_value    TEXT,
    new_value    TEXT,
    success      TINYINT(1)      NOT NULL DEFAULT 1,
    error_message VARCHAR(255),
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_created (created_at),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: notifications
-- ==============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_id     BIGINT,
    title       VARCHAR(200)    NOT NULL,
    message     TEXT            NOT NULL,
    type        VARCHAR(50),
    `read`      TINYINT(1)      NOT NULL DEFAULT 0,
    read_at     DATETIME,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_notif_user (user_id),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: device_logs
-- ==============================================================
CREATE TABLE IF NOT EXISTS device_logs (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT,
    ip_address      VARCHAR(50),
    hostname        VARCHAR(255),
    mac_address     VARCHAR(50),
    browser         VARCHAR(100),
    browser_version VARCHAR(50),
    operating_system VARCHAR(100),
    device_type     VARCHAR(50),
    user_agent      VARCHAR(500),
    connected_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    disconnected_at DATETIME,
    PRIMARY KEY (id),
    INDEX idx_device_user (user_id),
    INDEX idx_device_ip (ip_address),
    CONSTRAINT fk_device_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: network_logs
-- ==============================================================
CREATE TABLE IF NOT EXISTS network_logs (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT,
    ip_address      VARCHAR(50),
    hostname        VARCHAR(255),
    subnet          VARCHAR(50),
    network_id      VARCHAR(50),
    allowed         TINYINT(1)      NOT NULL DEFAULT 0,
    reason          VARCHAR(255),
    endpoint        VARCHAR(255),
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_netlog_user (user_id),
    INDEX idx_netlog_ip (ip_address),
    INDEX idx_netlog_allowed (allowed),
    CONSTRAINT fk_netlog_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- Table: refresh_tokens
-- ==============================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_id     BIGINT          NOT NULL,
    token       VARCHAR(500)    NOT NULL UNIQUE,
    expires_at  DATETIME        NOT NULL,
    revoked     TINYINT(1)      NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_refresh_user (user_id),
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
