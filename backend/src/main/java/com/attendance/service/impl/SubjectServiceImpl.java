package com.attendance.service.impl;
import com.attendance.dto.admin.SubjectRequest;
import com.attendance.dto.admin.SubjectResponse;
import com.attendance.entity.Department;
import com.attendance.entity.Faculty;
import com.attendance.entity.Subject;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.DepartmentRepository;
import com.attendance.repository.FacultyRepository;
import com.attendance.repository.SubjectRepository;
import com.attendance.service.interfaces.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    @Override @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjectRepository.existsByCode(request.getCode())) throw new BadRequestException("Subject code already exists");
        Department dept = departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Faculty faculty = request.getFacultyId() != null ? facultyRepository.findById(request.getFacultyId()).orElse(null) : null;

        Subject subject = Subject.builder()
            .name(request.getName()).code(request.getCode())
            .department(dept).faculty(faculty)
            .yearOfStudy(request.getYearOfStudy()).semester(request.getSemester())
            .creditHours(request.getCreditHours()).description(request.getDescription())
            .active(true).build();
        return mapToResponse(subjectRepository.save(subject));
    }

    @Override @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        if (!subject.getCode().equals(request.getCode()) && subjectRepository.existsByCode(request.getCode())) throw new BadRequestException("Subject code already exists");
        Department dept = departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Faculty faculty = request.getFacultyId() != null ? facultyRepository.findById(request.getFacultyId()).orElse(null) : null;

        subject.setName(request.getName());
        subject.setCode(request.getCode());
        subject.setDepartment(dept);
        subject.setFaculty(faculty);
        subject.setYearOfStudy(request.getYearOfStudy());
        subject.setSemester(request.getSemester());
        subject.setCreditHours(request.getCreditHours());
        subject.setDescription(request.getDescription());
        return mapToResponse(subjectRepository.save(subject));
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {
        return mapToResponse(subjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject not found")));
    }

    @Override @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        subject.setActive(false);
        subjectRepository.save(subject);
    }

    @Override
    public List<SubjectResponse> getSubjectsByDepartment(Long departmentId) {
        return subjectRepository.findByDepartmentIdAndActiveTrue(departmentId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
            .id(subject.getId()).name(subject.getName()).code(subject.getCode())
            .departmentId(subject.getDepartment().getId())
            .departmentName(subject.getDepartment().getName())
            .facultyId(subject.getFaculty() != null ? subject.getFaculty().getId() : null)
            .facultyName(subject.getFaculty() != null ? subject.getFaculty().getUser().getFullName() : null)
            .yearOfStudy(subject.getYearOfStudy()).semester(subject.getSemester())
            .creditHours(subject.getCreditHours()).description(subject.getDescription())
            .active(subject.isActive()).createdAt(subject.getCreatedAt()).build();
    }
}
