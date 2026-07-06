package com.attendance.controller;
import com.attendance.dto.ApiResponse;
import com.attendance.dto.admin.FacultyRequest;
import com.attendance.dto.admin.FacultyResponse;
import com.attendance.service.interfaces.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/faculty") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class FacultyController {
    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<ApiResponse<FacultyResponse>> create(@Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(ApiResponse.created("Faculty created", facultyService.createFaculty(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> update(@PathVariable Long id, @Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Faculty updated", facultyService.updateFaculty(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacultyResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.getFacultyById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(ApiResponse.noContent("Faculty deleted"));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.getFacultyByDepartment(departmentId)));
    }
}
