package com.attendance.service.interfaces;

import com.attendance.dto.PagedResponse;
import com.attendance.dto.attendance.AttendanceRequest;
import com.attendance.dto.attendance.AttendanceResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request, HttpServletRequest httpRequest);
    PagedResponse<AttendanceResponse> getStudentAttendance(Long studentId, int page, int size);
    PagedResponse<AttendanceResponse> getSessionAttendance(Long sessionId, int page, int size);
}
