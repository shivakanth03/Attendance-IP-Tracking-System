package com.attendance.service.interfaces;

public interface ReportService {
    byte[] generateSessionAttendancePdf(Long sessionId);
    byte[] generateSessionAttendanceExcel(Long sessionId);
    byte[] generateStudentAttendancePdf(Long studentId);
}
