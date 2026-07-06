package com.attendance.service.impl;

import com.attendance.dto.PagedResponse;
import com.attendance.dto.attendance.AttendanceRequest;
import com.attendance.dto.attendance.AttendanceResponse;
import com.attendance.entity.Attendance;
import com.attendance.entity.AttendanceSession;
import com.attendance.entity.Student;
import com.attendance.entity.User;
import com.attendance.enums.AttendanceStatus;
import com.attendance.enums.SessionStatus;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.DuplicateAttendanceException;
import com.attendance.exception.NetworkValidationException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.network.NetworkValidationService;
import com.attendance.qr.QrCodeService;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.AttendanceSessionRepository;
import com.attendance.repository.StudentRepository;
import com.attendance.repository.UserRepository;
import com.attendance.service.interfaces.AttendanceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceSessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final NetworkValidationService networkValidationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request, HttpServletRequest httpRequest) {
        // 1. Decrypt QR Payload
        String decryptedJson;
        try {
            decryptedJson = qrCodeService.decrypt(request.getQrPayload());
        } catch (Exception e) {
            throw new BadRequestException("Invalid or tampered QR code");
        }

        // 2. Parse payload
        Map<String, Object> payload;
        try {
            payload = objectMapper.readValue(decryptedJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse QR payload");
        }

        Long sessionId = Long.valueOf(payload.get("sessionId").toString());
        String token = (String) payload.get("token");
        LocalDateTime expiresAt = LocalDateTime.parse((String) payload.get("expiresAt"));

        // 3. Validate QR Expiry
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new BadRequestException("QR Code has expired. Please wait for the next QR.");
        }

        // 4. Validate Session
        AttendanceSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
            
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new BadRequestException("Attendance session is not active");
        }
        
        if (!session.getSessionToken().equals(token)) {
            throw new BadRequestException("Invalid session token");
        }

        // 5. Get Authenticated Student
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        Student student = studentRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        // 6. Duplicate Check
        if (attendanceRepository.existsByStudentIdAndSessionId(student.getId(), session.getId())) {
            throw new DuplicateAttendanceException("Attendance already marked for this session");
        }

        // 7. Network Validation (Strict Campus Mode)
        String clientIp = networkValidationService.extractClientIp(httpRequest);
        String networkStatus = "VALID";
        try {
            networkValidationService.validateClientNetwork(httpRequest);
        } catch (NetworkValidationException e) {
            networkStatus = "INVALID_IP_BLOCKED";
            throw e; // Throw the exception to block attendance
        }

        // 8. Save Attendance
        Attendance attendance = Attendance.builder()
            .student(student)
            .session(session)
            .status(AttendanceStatus.PRESENT)
            .ipAddress(clientIp)
            .deviceInfo(request.getDeviceInfo())
            .browser(request.getBrowser())
            .operatingSystem(request.getOperatingSystem())
            .networkStatus(networkStatus)
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .markedAt(LocalDateTime.now())
            .build();
            
        attendance = attendanceRepository.save(attendance);

        // 9. Notify Admin Dashboard
        messagingTemplate.convertAndSend("/topic/attendance", "NEW_ATTENDANCE:" + attendance.getId());

        return mapToResponse(attendance);
    }

    @Override
    public PagedResponse<AttendanceResponse> getStudentAttendance(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attendance> attendances = attendanceRepository.findByStudentId(studentId, pageable);
        return createPagedResponse(attendances);
    }

    @Override
    public PagedResponse<AttendanceResponse> getSessionAttendance(Long sessionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Attendance> attendances = attendanceRepository.findBySessionId(sessionId, pageable);
        return createPagedResponse(attendances);
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
            .id(attendance.getId())
            .studentId(attendance.getStudent().getId())
            .studentName(attendance.getStudent().getUser().getFullName())
            .rollNumber(attendance.getStudent().getRollNumber())
            .sessionId(attendance.getSession().getId())
            .subjectName(attendance.getSession().getSubject().getName())
            .status(attendance.getStatus().name())
            .markedAt(attendance.getMarkedAt())
            .ipAddress(attendance.getIpAddress())
            .networkStatus(attendance.getNetworkStatus())
            .build();
    }

    private PagedResponse<AttendanceResponse> createPagedResponse(Page<Attendance> page) {
        List<AttendanceResponse> content = page.getContent().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        return PagedResponse.<AttendanceResponse>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
