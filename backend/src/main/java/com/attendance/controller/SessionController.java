package com.attendance.controller;
import com.attendance.dto.ApiResponse;
import com.attendance.dto.attendance.SessionRequest;
import com.attendance.dto.attendance.SessionResponse;
import com.attendance.service.interfaces.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/sessions") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> create(@Valid @RequestBody SessionRequest request) {
        return ResponseEntity.ok(ApiResponse.created("Session created", sessionService.createSession(request)));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<SessionResponse>> close(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Session closed", sessionService.closeSession(id)));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<SessionResponse>> pause(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Session paused", sessionService.pauseSession(id)));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<ApiResponse<SessionResponse>> resume(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Session resumed", sessionService.resumeSession(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(sessionService.getSessionById(id)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(sessionService.getActiveSessions()));
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getQrImage(@PathVariable Long id) {
        byte[] qrImage = sessionService.getSessionQrImage(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr-" + id + ".png\"")
            .contentType(MediaType.IMAGE_PNG)
            .body(qrImage);
    }
}
