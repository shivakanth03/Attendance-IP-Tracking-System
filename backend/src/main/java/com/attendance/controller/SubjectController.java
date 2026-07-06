package com.attendance.controller;
import com.attendance.dto.ApiResponse;
import com.attendance.dto.admin.SubjectRequest;
import com.attendance.dto.admin.SubjectResponse;
import com.attendance.service.interfaces.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/subjects") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> create(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(ApiResponse.created("Subject created", subjectService.createSubject(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> update(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Subject updated", subjectService.updateSubject(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjectById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.noContent("Subject deleted"));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success(subjectService.getSubjectsByDepartment(departmentId)));
    }
}
