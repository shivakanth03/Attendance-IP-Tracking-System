package com.attendance.repository;
import com.attendance.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByActiveTrue();
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
