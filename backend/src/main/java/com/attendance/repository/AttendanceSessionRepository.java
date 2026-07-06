package com.attendance.repository;
import com.attendance.entity.AttendanceSession;
import com.attendance.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    Optional<AttendanceSession> findBySessionToken(String token);
    List<AttendanceSession> findBySessionDateAndStatus(LocalDate date, SessionStatus status);
    List<AttendanceSession> findByStatusIn(List<SessionStatus> statuses);
    List<AttendanceSession> findTop10ByOrderByCreatedAtDesc();
    long countBySessionDateAndStatus(LocalDate date, SessionStatus status);
}
