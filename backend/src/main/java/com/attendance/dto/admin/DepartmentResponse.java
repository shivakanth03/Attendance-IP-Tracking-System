package com.attendance.dto.admin;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class DepartmentResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String headOfDepartment;
    private boolean active;
    private LocalDateTime createdAt;
}
