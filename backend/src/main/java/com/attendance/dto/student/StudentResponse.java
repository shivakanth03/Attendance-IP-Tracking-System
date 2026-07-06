package com.attendance.dto.student;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class StudentResponse {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String profileImage;
    
    private String rollNumber;
    private String registerNumber;
    
    private Long departmentId;
    private String departmentName;
    
    private String yearOfStudy;
    private String section;
    private String semester;
    private Integer admissionYear;
    
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String parentName;
    private String parentPhone;
    
    private boolean active;
    private LocalDateTime createdAt;
}
