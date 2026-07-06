package com.attendance.security;

import com.attendance.entity.Student;
import com.attendance.entity.User;
import com.attendance.repository.StudentRepository;
import com.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    /**
     * Checks if the currently authenticated user is the student with the given studentId.
     */
    public boolean isStudent(Long studentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }

        Student student = studentRepository.findByUserId(user.getId()).orElse(null);
        return student != null && student.getId().equals(studentId);
    }
}
