package com.attendance.controller;

import com.attendance.service.interfaces.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Export attendance reports")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/session/{sessionId}/pdf")
    @Operation(summary = "Export session attendance as PDF")
    public ResponseEntity<byte[]> getSessionPdf(@PathVariable Long sessionId) {
        byte[] pdf = reportService.generateSessionAttendancePdf(sessionId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"session-" + sessionId + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/session/{sessionId}/excel")
    @Operation(summary = "Export session attendance as Excel")
    public ResponseEntity<byte[]> getSessionExcel(@PathVariable Long sessionId) {
        byte[] excel = reportService.generateSessionAttendanceExcel(sessionId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"session-" + sessionId + ".xlsx\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel);
    }

    @GetMapping("/student/{studentId}/pdf")
    @Operation(summary = "Export student attendance as PDF")
    public ResponseEntity<byte[]> getStudentPdf(@PathVariable Long studentId) {
        byte[] pdf = reportService.generateStudentAttendancePdf(studentId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"student-" + studentId + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
