package com.attendance.repository;
import com.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByStudentIdAndSessionId(Long studentId, Long sessionId);
    Optional<Attendance> findByStudentIdAndSessionId(Long studentId, Long sessionId);
    Page<Attendance> findByStudentId(Long studentId, Pageable pageable);
    Page<Attendance> findBySessionId(Long sessionId, Pageable pageable);
    List<Attendance> findBySessionId(Long sessionId);
    @Query("SELECT COUNT(a) FROM Attendance a JOIN a.session s WHERE DATE(s.sessionDate) = CURRENT_DATE")
    long countTodayAttendance();
    @Query("SELECT COUNT(DISTINCT a.student.id) FROM Attendance a JOIN a.session s WHERE DATE(s.sessionDate) = CURRENT_DATE")
    long countTodayPresentStudents();
    @Query("SELECT a FROM Attendance a WHERE a.markedAt BETWEEN :start AND :end")
    List<Attendance> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
