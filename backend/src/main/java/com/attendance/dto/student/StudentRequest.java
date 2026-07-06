package com.attendance.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private String phone;
    
    @NotBlank(message = "Roll number is required")
    private String rollNumber;
    
    private String registerNumber;
    private Long departmentId;
    private String yearOfStudy;
    private String section;
    private String semester;
    private Integer admissionYear;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String parentName;
    private String parentPhone;
}
