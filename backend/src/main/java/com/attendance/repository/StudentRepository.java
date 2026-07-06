package com.attendance.repository;
import com.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNumber);
    Optional<Student> findByUserId(Long userId);
    boolean existsByRollNumber(String rollNumber);
    Page<Student> findByDepartmentIdAndActiveTrue(Long departmentId, Pageable pageable);
    @Query("""
        SELECT s FROM Student s JOIN s.user u
        WHERE s.active = true AND (
          LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR
          LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR
          LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    Page<Student> searchStudents(@Param("q") String query, Pageable pageable);
    long countByDepartmentIdAndActiveTrue(Long departmentId);
}
