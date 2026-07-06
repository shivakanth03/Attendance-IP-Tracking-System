package com.attendance.service.impl;

import com.attendance.entity.Attendance;
import com.attendance.entity.AttendanceSession;
import com.attendance.entity.Student;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.AttendanceSessionRepository;
import com.attendance.repository.StudentRepository;
import com.attendance.service.interfaces.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Override
    public byte[] generateSessionAttendancePdf(Long sessionId) {
        // Basic placeholder for PDF Generation using iText
        // A complete implementation would build a table with student roll numbers, names, and attendance status
        AttendanceSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        return ("PDF Report for Session " + session.getId()).getBytes();
    }

    @Override
    public byte[] generateSessionAttendanceExcel(Long sessionId) {
        // Basic placeholder for Excel Generation using Apache POI
        AttendanceSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        return ("Excel Report for Session " + session.getId()).getBytes();
    }

    @Override
    public byte[] generateStudentAttendancePdf(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return ("PDF Report for Student " + student.getRollNumber()).getBytes();
    }
}
