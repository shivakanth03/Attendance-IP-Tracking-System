package com.attendance.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalStudents;
    private long totalFaculty;
    private long totalDepartments;
    private long activeSessions;
    private long todayAttendance;
    private long todayPresentStudents;
    private long onlineUsers;
    private double overallAttendancePercentage;
}
