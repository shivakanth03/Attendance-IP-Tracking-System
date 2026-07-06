package com.attendance.service.impl;

import com.attendance.dto.admin.DashboardStatsResponse;
import com.attendance.enums.Role;
import com.attendance.enums.SessionStatus;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.AttendanceSessionRepository;
import com.attendance.repository.DepartmentRepository;
import com.attendance.repository.UserRepository;
import com.attendance.service.interfaces.DashboardService;
import com.attendance.socket.SocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final SocketServer socketServer;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalStudents = userRepository.countByRoleAndActiveTrue(Role.STUDENT);
        long totalFaculty = userRepository.countByRoleAndActiveTrue(Role.ADMIN);
        long totalDepartments = departmentRepository.count();
        long activeSessions = sessionRepository.countBySessionDateAndStatus(LocalDate.now(), SessionStatus.ACTIVE);
        long todayAttendance = attendanceRepository.countTodayAttendance();
        long todayPresentStudents = attendanceRepository.countTodayPresentStudents();

        double overallAttendancePercentage = 0.0;
        if (totalStudents > 0) {
            // Simplistic overall percentage calculation for dashboard overview
            // In a real scenario, this would be total possible attendance vs actual
            overallAttendancePercentage = ((double) todayPresentStudents / totalStudents) * 100.0;
        }

        return DashboardStatsResponse.builder()
                .totalStudents(totalStudents)
                .totalFaculty(totalFaculty)
                .totalDepartments(totalDepartments)
                .activeSessions(activeSessions)
                .todayAttendance(todayAttendance)
                .todayPresentStudents(todayPresentStudents)
                .onlineUsers(socketServer.getConnectedClientCount())
                .overallAttendancePercentage(Math.round(overallAttendancePercentage * 100.0) / 100.0)
                .build();
    }
}
