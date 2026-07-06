package com.attendance.dto.attendance;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SessionRequest {
    @NotNull private Long subjectId;
    @NotNull private Long departmentId;
    private String yearOfStudy;
    private String section;
    @NotNull private LocalDate sessionDate;
    @NotNull private LocalTime startTime;
    private LocalTime endTime;
    private int expiryMinutes = 5;
}
