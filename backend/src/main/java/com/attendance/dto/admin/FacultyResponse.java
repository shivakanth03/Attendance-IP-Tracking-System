package com.attendance.dto.admin;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class FacultyResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String employeeId;
    private Long departmentId;
    private String departmentName;
    private String designation;
    private String qualification;
    private Integer experienceYears;
    private boolean active;
}
