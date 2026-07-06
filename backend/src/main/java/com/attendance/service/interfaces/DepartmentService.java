package com.attendance.service.interfaces;
import com.attendance.dto.admin.DepartmentRequest;
import com.attendance.dto.admin.DepartmentResponse;
import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
    DepartmentResponse getDepartmentById(Long id);
    void deleteDepartment(Long id);
    List<DepartmentResponse> getAllDepartments();
}
