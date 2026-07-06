package com.attendance.config;

import com.attendance.entity.User;
import com.attendance.enums.Role;
import com.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Database is empty. Seeding default Super Admin user...");
            
            User superAdmin = User.builder()
                .email("admin@college.edu")
                .password(passwordEncoder.encode("Admin@1234"))
                .fullName("Super Administrator")
                .phone("9876543210")
                .role(Role.SUPER_ADMIN)
                .active(true)
                .build();
                
            userRepository.save(superAdmin);
            log.info("Super Admin user created: admin@college.edu / Admin@1234");
        } else {
            log.info("Database already contains users. Skipping seeder.");
        }
    }
}
