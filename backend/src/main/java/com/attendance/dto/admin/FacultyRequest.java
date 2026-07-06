package com.attendance.dto.admin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacultyRequest {
    @NotBlank @Email private String email;
    @NotBlank private String fullName;
    private String phone;
    @NotBlank private String employeeId;
    private Long departmentId;
    private String designation;
    private String qualification;
    private Integer experienceYears;
}
