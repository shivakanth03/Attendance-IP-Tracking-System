package com.attendance.dto.attendance;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder
public class SessionResponse {
    private Long id;
    private String subjectName;
    private String departmentName;
    private String createdBy;
    private String yearOfStudy;
    private String section;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int expiryMinutes;
    private LocalDateTime qrExpiresAt;
    private String status;
    private String sessionToken;
    private LocalDateTime createdAt;
}
