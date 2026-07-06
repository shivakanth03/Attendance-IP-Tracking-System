package com.attendance.controller;

import com.attendance.dto.ApiResponse;
import com.attendance.dto.PagedResponse;
import com.attendance.dto.student.StudentRequest;
import com.attendance.dto.student.StudentResponse;
import com.attendance.service.interfaces.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create a new student")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(ApiResponse.created("Student created successfully", 
            studentService.createStudent(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update an existing student")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", 
            studentService.updateStudent(id, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete (soft delete) a student")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.noContent("Student deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all students with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents(page, size)));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get students by department")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponse>>> getStudentsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            studentService.getStudentsByDepartment(departmentId, page, size)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search students by name, email, or roll number")
    public ResponseEntity<ApiResponse<PagedResponse<StudentResponse>>> searchStudents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            studentService.searchStudents(query, page, size)));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Upload student profile photo")
    public ResponseEntity<ApiResponse<Void>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        studentService.uploadProfileImage(id, file);
        return ResponseEntity.ok(ApiResponse.noContent("Photo uploaded successfully"));
    }
}
