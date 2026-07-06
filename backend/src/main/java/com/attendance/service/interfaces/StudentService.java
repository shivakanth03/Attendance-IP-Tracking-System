package com.attendance.service.interfaces;
import com.attendance.dto.PagedResponse;
import com.attendance.dto.student.StudentRequest;
import com.attendance.dto.student.StudentResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse updateStudent(Long id, StudentRequest request);
    StudentResponse getStudentById(Long id);
    void deleteStudent(Long id);
    PagedResponse<StudentResponse> getAllStudents(int page, int size);
    PagedResponse<StudentResponse> getStudentsByDepartment(Long departmentId, int page, int size);
    PagedResponse<StudentResponse> searchStudents(String query, int page, int size);
    void uploadProfileImage(Long id, MultipartFile file);
}
