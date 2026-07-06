package com.attendance.service.impl;
import com.attendance.dto.admin.DepartmentRequest;
import com.attendance.dto.admin.DepartmentResponse;
import com.attendance.entity.Department;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.DepartmentRepository;
import com.attendance.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) throw new BadRequestException("Department name already exists");
        if (departmentRepository.existsByCode(request.getCode())) throw new BadRequestException("Department code already exists");

        Department dept = Department.builder()
            .name(request.getName())
            .code(request.getCode())
            .description(request.getDescription())
            .headOfDepartment(request.getHeadOfDepartment())
            .active(true).build();
        return mapToResponse(departmentRepository.save(dept));
    }

    @Override @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        if (!dept.getName().equals(request.getName()) && departmentRepository.existsByName(request.getName())) throw new BadRequestException("Department name already exists");
        if (!dept.getCode().equals(request.getCode()) && departmentRepository.existsByCode(request.getCode())) throw new BadRequestException("Department code already exists");

        dept.setName(request.getName());
        dept.setCode(request.getCode());
        dept.setDescription(request.getDescription());
        dept.setHeadOfDepartment(request.getHeadOfDepartment());
        return mapToResponse(departmentRepository.save(dept));
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        return mapToResponse(departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found")));
    }

    @Override @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        dept.setActive(false);
        departmentRepository.save(dept);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findByActiveTrue().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private DepartmentResponse mapToResponse(Department dept) {
        return DepartmentResponse.builder()
            .id(dept.getId()).name(dept.getName()).code(dept.getCode())
            .description(dept.getDescription()).headOfDepartment(dept.getHeadOfDepartment())
            .active(dept.isActive()).createdAt(dept.getCreatedAt()).build();
    }
}
