package com.attendance.service.impl;

import com.attendance.dto.attendance.SessionRequest;
import com.attendance.dto.attendance.SessionResponse;
import com.attendance.entity.AttendanceSession;
import com.attendance.entity.Department;
import com.attendance.entity.Subject;
import com.attendance.entity.User;
import com.attendance.enums.SessionStatus;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.qr.QrCodeService;
import com.attendance.repository.AttendanceSessionRepository;
import com.attendance.repository.DepartmentRepository;
import com.attendance.repository.SubjectRepository;
import com.attendance.repository.UserRepository;
import com.attendance.service.interfaces.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final AttendanceSessionRepository sessionRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SessionResponse createSession(SessionRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String sessionToken = UUID.randomUUID().toString();
        
        AttendanceSession session = AttendanceSession.builder()
            .subject(subject)
            .department(department)
            .createdBy(currentUser)
            .yearOfStudy(request.getYearOfStudy())
            .section(request.getSection())
            .sessionDate(request.getSessionDate())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .expiryMinutes(request.getExpiryMinutes())
            .status(SessionStatus.ACTIVE)
            .sessionToken(sessionToken)
            .qrExpiresAt(LocalDateTime.now().plusMinutes(request.getExpiryMinutes()))
            .build();
            
        session = sessionRepository.save(session);
        
        // Notify dashboard via WebSocket
        messagingTemplate.convertAndSend("/topic/session", "SESSION_CREATED:" + session.getId());
        
        return mapToResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse closeSession(Long id) {
        AttendanceSession session = getSession(id);
        session.setStatus(SessionStatus.CLOSED);
        session = sessionRepository.save(session);
        messagingTemplate.convertAndSend("/topic/session", "SESSION_CLOSED:" + id);
        return mapToResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse pauseSession(Long id) {
        AttendanceSession session = getSession(id);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new BadRequestException("Only active sessions can be paused");
        }
        session.setStatus(SessionStatus.PAUSED);
        session = sessionRepository.save(session);
        messagingTemplate.convertAndSend("/topic/session", "SESSION_PAUSED:" + id);
        return mapToResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse resumeSession(Long id) {
        AttendanceSession session = getSession(id);
        if (session.getStatus() != SessionStatus.PAUSED) {
            throw new BadRequestException("Only paused sessions can be resumed");
        }
        session.setStatus(SessionStatus.ACTIVE);
        // Reset QR expiry time on resume
        session.setQrExpiresAt(LocalDateTime.now().plusMinutes(session.getExpiryMinutes()));
        session = sessionRepository.save(session);
        messagingTemplate.convertAndSend("/topic/session", "SESSION_RESUMED:" + id);
        return mapToResponse(session);
    }

    @Override
    public SessionResponse getSessionById(Long id) {
        return mapToResponse(getSession(id));
    }

    @Override
    public List<SessionResponse> getActiveSessions() {
        return sessionRepository.findByStatusIn(List.of(SessionStatus.ACTIVE, SessionStatus.PAUSED))
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public byte[] getSessionQrImage(Long id) {
        AttendanceSession session = getSession(id);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new BadRequestException("Cannot generate QR for inactive session");
        }
        
        if (session.getQrExpiresAt().isBefore(LocalDateTime.now())) {
            session.setQrExpiresAt(LocalDateTime.now().plusMinutes(session.getExpiryMinutes()));
            sessionRepository.save(session);
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("sessionId", session.getId());
            payload.put("token", session.getSessionToken());
            payload.put("expiresAt", session.getQrExpiresAt().toString());
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            return qrCodeService.generateQrCode(jsonPayload); // AES-256 encrypted inside
        } catch (JsonProcessingException e) {
            log.error("Failed to generate QR payload JSON", e);
            throw new RuntimeException("Failed to generate QR Code");
        }
    }

    private AttendanceSession getSession(Long id) {
        return sessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    private SessionResponse mapToResponse(AttendanceSession session) {
        return SessionResponse.builder()
            .id(session.getId())
            .subjectName(session.getSubject().getName())
            .departmentName(session.getDepartment().getName())
            .createdBy(session.getCreatedBy().getFullName())
            .yearOfStudy(session.getYearOfStudy())
            .section(session.getSection())
            .sessionDate(session.getSessionDate())
            .startTime(session.getStartTime())
            .endTime(session.getEndTime())
            .expiryMinutes(session.getExpiryMinutes())
            .qrExpiresAt(session.getQrExpiresAt())
            .status(session.getStatus().name())
            .sessionToken(session.getSessionToken())
            .createdAt(session.getCreatedAt())
            .build();
    }
}
