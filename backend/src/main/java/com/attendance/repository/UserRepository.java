package com.attendance.repository;

import com.attendance.entity.User;
import com.attendance.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndActiveTrue(String email);

    Optional<User> findByPasswordResetToken(String token);

    boolean existsByEmail(String email);

    long countByRoleAndActiveTrue(Role role);

}
