package com.attendance.dto.admin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectRequest {
    @NotBlank private String name;
    @NotBlank private String code;
    @NotNull private Long departmentId;
    private Long facultyId;
    private String yearOfStudy;
    private String semester;
    private Integer creditHours;
    private String description;
}
