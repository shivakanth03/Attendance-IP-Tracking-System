package com.attendance.dto.admin;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class SubjectResponse {
    private Long id;
    private String name;
    private String code;
    private Long departmentId;
    private String departmentName;
    private Long facultyId;
    private String facultyName;
    private String yearOfStudy;
    private String semester;
    private Integer creditHours;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
}
