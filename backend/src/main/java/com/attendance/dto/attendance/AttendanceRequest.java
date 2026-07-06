package com.attendance.dto.attendance;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttendanceRequest {
    @NotBlank(message = "QR Payload is required")
    private String qrPayload; // This is the AES encrypted QR string
    
    private Double latitude;
    private Double longitude;
    private String browser;
    private String operatingSystem;
    private String deviceInfo;
}
