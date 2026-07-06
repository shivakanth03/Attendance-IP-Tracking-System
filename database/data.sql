-- ==============================================================
-- Smart Attendance System - Seed Data (Development)
-- ==============================================================
USE attendance_db;

-- ==============================================================
-- Default Users
-- Passwords are BCrypt encoded (strength=12):
--   Admin@1234  → $2a$12$...
--   Staff@1234  → $2a$12$...
--   Student@1234 → $2a$12$...
-- ==============================================================

INSERT INTO users (email, password, full_name, phone, role, active)
VALUES
-- Super Admin
('superadmin@college.edu',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewdBHBt7dkJ/MQFO',
 'Super Administrator', '9876543210', 'SUPER_ADMIN', 1),
-- Admin / Staff
('staff@college.edu',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewdBHBt7dkJ/MQFO',
 'John Staff', '9876543211', 'ADMIN', 1),
-- Student 1
('student1@college.edu',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewdBHBt7dkJ/MQFO',
 'Alice Student', '9876543212', 'STUDENT', 1),
-- Student 2
('student2@college.edu',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewdBHBt7dkJ/MQFO',
 'Bob Student', '9876543213', 'STUDENT', 1),
-- Student 3
('student3@college.edu',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/lewdBHBt7dkJ/MQFO',
 'Carol Student', '9876543214', 'STUDENT', 1);

-- ==============================================================
-- Departments
-- ==============================================================
INSERT INTO departments (name, code, description, head_of_department, active)
VALUES
('Computer Science & Engineering', 'CSE', 'Core computer science department', 'Dr. Smith', 1),
('Electronics & Communication',    'ECE', 'Electronics engineering department', 'Dr. Johnson', 1),
('Mechanical Engineering',          'MECH', 'Mechanical engineering department', 'Dr. Williams', 1),
('Civil Engineering',               'CIVIL', 'Civil engineering department', 'Dr. Brown', 1),
('Information Technology',          'IT', 'Information technology department', 'Dr. Davis', 1);

-- ==============================================================
-- Faculty (linked to Admin user)
-- ==============================================================
INSERT INTO faculty (user_id, employee_id, department_id, designation, qualification, experience_years, active)
VALUES
(2, 'EMP001', 1, 'Associate Professor', 'M.Tech, PhD', 8, 1);

-- ==============================================================
-- Subjects
-- ==============================================================
INSERT INTO subjects (name, code, department_id, faculty_id, year_of_study, semester, credit_hours, active)
VALUES
('Data Structures & Algorithms', 'CSE201', 1, 1, '2', '3', 4, 1),
('Database Management Systems',  'CSE301', 1, 1, '3', '5', 4, 1),
('Operating Systems',            'CSE302', 1, 1, '3', '5', 3, 1),
('Computer Networks',            'CSE401', 1, 1, '4', '7', 3, 1),
('Web Technologies',             'CSE305', 1, 1, '3', '6', 3, 1);

-- ==============================================================
-- Students (linked to student users)
-- ==============================================================
INSERT INTO students (user_id, roll_number, register_number, department_id,
                      year_of_study, section, semester, admission_year, gender, active)
VALUES
(3, 'CSE21001', 'REG21CS001', 1, '3', 'A', '5', 2021, 'Female', 1),
(4, 'CSE21002', 'REG21CS002', 1, '3', 'A', '5', 2021, 'Male', 1),
(5, 'CSE21003', 'REG21CS003', 1, '3', 'B', '5', 2021, 'Female', 1);
