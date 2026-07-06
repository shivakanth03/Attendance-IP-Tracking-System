package com.attendance.service.impl;

import com.attendance.dto.PagedResponse;
import com.attendance.dto.student.StudentRequest;
import com.attendance.dto.student.StudentResponse;
import com.attendance.entity.Department;
import com.attendance.entity.Student;
import com.attendance.entity.User;
import com.attendance.enums.AuditAction;
import com.attendance.enums.Role;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.AuditLogRepository;
import com.attendance.repository.DepartmentRepository;
import com.attendance.repository.StudentRepository;
import com.attendance.repository.UserRepository;
import com.attendance.service.interfaces.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository; // Can be extracted to a separate AuditService

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        log.info("Creating new student with roll number: {}", request.getRollNumber());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new BadRequestException("Roll number already exists");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        // Create User account for student
        User user = User.builder()
            .email(request.getEmail())
            // Default password is roll number, MUST change on first login
            .password(passwordEncoder.encode(request.getRollNumber())) 
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .role(Role.STUDENT)
            .active(true)
            .build();
        user = userRepository.save(user);

        // Create Student profile
        Student student = Student.builder()
            .user(user)
            .rollNumber(request.getRollNumber())
            .registerNumber(request.getRegisterNumber())
            .department(department)
            .yearOfStudy(request.getYearOfStudy())
            .section(request.getSection())
            .semester(request.getSemester())
            .admissionYear(request.getAdmissionYear())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .address(request.getAddress())
            .parentName(request.getParentName())
            .parentPhone(request.getParentPhone())
            .active(true)
            .build();
        student = studentRepository.save(student);

        return mapToResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found", id));
            
        User user = student.getUser();
        
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (!student.getRollNumber().equals(request.getRollNumber()) && studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new BadRequestException("Roll number already in use");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        student.setRollNumber(request.getRollNumber());
        student.setRegisterNumber(request.getRegisterNumber());
        student.setDepartment(department);
        student.setYearOfStudy(request.getYearOfStudy());
        student.setSection(request.getSection());
        student.setSemester(request.getSemester());
        student.setAdmissionYear(request.getAdmissionYear());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setAddress(request.getAddress());
        student.setParentName(request.getParentName());
        student.setParentPhone(request.getParentPhone());
        student = studentRepository.save(student);

        return mapToResponse(student);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found", id));
        return mapToResponse(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found", id));
        
        student.setActive(false);
        student.getUser().setActive(false);
        
        studentRepository.save(student);
        userRepository.save(student.getUser());
    }

    @Override
    public PagedResponse<StudentResponse> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findAll(pageable);
        return createPagedResponse(students);
    }

    @Override
    public PagedResponse<StudentResponse> getStudentsByDepartment(Long departmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.findByDepartmentIdAndActiveTrue(departmentId, pageable);
        return createPagedResponse(students);
    }

    @Override
    public PagedResponse<StudentResponse> searchStudents(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = studentRepository.searchStudents(query, pageable);
        return createPagedResponse(students);
    }

    @Override
    public void uploadProfileImage(Long id, MultipartFile file) {
        // Implementation would use a FileStorageService
        // For now, left as a placeholder
        throw new UnsupportedOperationException("File upload not implemented yet");
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
            .id(student.getId())
            .userId(student.getUser().getId())
            .email(student.getUser().getEmail())
            .fullName(student.getUser().getFullName())
            .phone(student.getUser().getPhone())
            .profileImage(student.getUser().getProfileImage())
            .rollNumber(student.getRollNumber())
            .registerNumber(student.getRegisterNumber())
            .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
            .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
            .yearOfStudy(student.getYearOfStudy())
            .section(student.getSection())
            .semester(student.getSemester())
            .admissionYear(student.getAdmissionYear())
            .dateOfBirth(student.getDateOfBirth())
            .gender(student.getGender())
            .address(student.getAddress())
            .parentName(student.getParentName())
            .parentPhone(student.getParentPhone())
            .active(student.isActive())
            .createdAt(student.getCreatedAt())
            .build();
    }
    
    private PagedResponse<StudentResponse> createPagedResponse(Page<Student> page) {
        List<StudentResponse> content = page.getContent().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
            
        return PagedResponse.<StudentResponse>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
