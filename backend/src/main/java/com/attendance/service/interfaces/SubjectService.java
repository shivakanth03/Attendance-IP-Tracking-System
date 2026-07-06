package com.attendance.service.interfaces;
import com.attendance.dto.admin.SubjectRequest;
import com.attendance.dto.admin.SubjectResponse;
import java.util.List;

public interface SubjectService {
    SubjectResponse createSubject(SubjectRequest request);
    SubjectResponse updateSubject(Long id, SubjectRequest request);
    SubjectResponse getSubjectById(Long id);
    void deleteSubject(Long id);
    List<SubjectResponse> getSubjectsByDepartment(Long departmentId);
}
