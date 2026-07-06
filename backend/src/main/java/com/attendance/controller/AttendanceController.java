package com.attendance.controller;
import com.attendance.dto.ApiResponse;
import com.attendance.dto.PagedResponse;
import com.attendance.dto.attendance.AttendanceRequest;
import com.attendance.dto.attendance.AttendanceResponse;
import com.attendance.service.interfaces.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/attendance") @RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> mark(
            @Valid @RequestBody AttendanceRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.created("Attendance marked successfully", 
            attendanceService.markAttendance(request, httpRequest)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or @securityService.isStudent(#studentId)")
    public ResponseEntity<ApiResponse<PagedResponse<AttendanceResponse>>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getStudentAttendance(studentId, page, size)));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<AttendanceResponse>>> getSessionAttendance(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getSessionAttendance(sessionId, page, size)));
    }
}
