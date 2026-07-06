package com.attendance.dto.attendance;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private Long sessionId;
    private String subjectName;
    private String status;
    private LocalDateTime markedAt;
    private String ipAddress;
    private String networkStatus;
}
