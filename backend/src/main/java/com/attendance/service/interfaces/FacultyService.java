package com.attendance.service.interfaces;
import com.attendance.dto.admin.FacultyRequest;
import com.attendance.dto.admin.FacultyResponse;
import java.util.List;

public interface FacultyService {
    FacultyResponse createFaculty(FacultyRequest request);
    FacultyResponse updateFaculty(Long id, FacultyRequest request);
    FacultyResponse getFacultyById(Long id);
    void deleteFaculty(Long id);
    List<FacultyResponse> getFacultyByDepartment(Long departmentId);
}
