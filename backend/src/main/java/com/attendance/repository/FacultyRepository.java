package com.attendance.repository;
import com.attendance.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findByDepartmentIdAndActiveTrue(Long departmentId);
    Optional<Faculty> findByUserId(Long userId);
    boolean existsByEmployeeId(String employeeId);
}
