package com.attendance.service.impl;
import com.attendance.dto.admin.FacultyRequest;
import com.attendance.dto.admin.FacultyResponse;
import com.attendance.entity.Department;
import com.attendance.entity.Faculty;
import com.attendance.entity.User;
import com.attendance.enums.Role;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.DepartmentRepository;
import com.attendance.repository.FacultyRepository;
import com.attendance.repository.UserRepository;
import com.attendance.service.interfaces.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override @Transactional
    public FacultyResponse createFaculty(FacultyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new BadRequestException("Email already exists");
        if (facultyRepository.existsByEmployeeId(request.getEmployeeId())) throw new BadRequestException("Employee ID already exists");
        Department dept = request.getDepartmentId() != null ? departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Department not found")) : null;

        User user = User.builder()
            .email(request.getEmail()).password(passwordEncoder.encode(request.getEmployeeId()))
            .fullName(request.getFullName()).phone(request.getPhone())
            .role(Role.ADMIN).active(true).build();
        user = userRepository.save(user);

        Faculty faculty = Faculty.builder()
            .user(user).employeeId(request.getEmployeeId())
            .department(dept).designation(request.getDesignation())
            .qualification(request.getQualification()).experienceYears(request.getExperienceYears())
            .active(true).build();
        return mapToResponse(facultyRepository.save(faculty));
    }

    @Override @Transactional
    public FacultyResponse updateFaculty(Long id, FacultyRequest request) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        User user = faculty.getUser();
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) throw new BadRequestException("Email already exists");
        if (!faculty.getEmployeeId().equals(request.getEmployeeId()) && facultyRepository.existsByEmployeeId(request.getEmployeeId())) throw new BadRequestException("Employee ID already exists");
        Department dept = request.getDepartmentId() != null ? departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Department not found")) : null;

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        faculty.setEmployeeId(request.getEmployeeId());
        faculty.setDepartment(dept);
        faculty.setDesignation(request.getDesignation());
        faculty.setQualification(request.getQualification());
        faculty.setExperienceYears(request.getExperienceYears());
        return mapToResponse(facultyRepository.save(faculty));
    }

    @Override
    public FacultyResponse getFacultyById(Long id) {
        return mapToResponse(facultyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faculty not found")));
    }

    @Override @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        faculty.setActive(false);
        faculty.getUser().setActive(false);
        facultyRepository.save(faculty);
        userRepository.save(faculty.getUser());
    }

    @Override
    public List<FacultyResponse> getFacultyByDepartment(Long departmentId) {
        return facultyRepository.findByDepartmentIdAndActiveTrue(departmentId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private FacultyResponse mapToResponse(Faculty faculty) {
        return FacultyResponse.builder()
            .id(faculty.getId()).userId(faculty.getUser().getId())
            .email(faculty.getUser().getEmail()).fullName(faculty.getUser().getFullName())
            .phone(faculty.getUser().getPhone()).employeeId(faculty.getEmployeeId())
            .departmentId(faculty.getDepartment() != null ? faculty.getDepartment().getId() : null)
            .departmentName(faculty.getDepartment() != null ? faculty.getDepartment().getName() : null)
            .designation(faculty.getDesignation()).qualification(faculty.getQualification())
            .experienceYears(faculty.getExperienceYears()).active(faculty.isActive()).build();
    }
}
