package com.attendance.service.impl;

import com.attendance.dto.auth.*;
import com.attendance.entity.User;
import com.attendance.enums.AuditAction;
import com.attendance.entity.AuditLog;
import com.attendance.exception.BadRequestException;
import com.attendance.exception.ResourceNotFoundException;
import com.attendance.repository.AuditLogRepository;
import com.attendance.repository.UserRepository;
import com.attendance.security.CustomUserDetails;
import com.attendance.security.JwtService;
import com.attendance.service.interfaces.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            userRepository.save(user);

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Log action
            logAudit(user, AuditAction.LOGIN, "User logged in successfully", getClientIp(httpRequest));

            return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(mapToUserProfile(user))
                .build();
        } catch (Exception e) {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                if (user.getFailedLoginAttempts() >= 5) {
                    user.setLocked(true);
                }
                userRepository.save(user);
                logAudit(user, AuditAction.LOGIN_FAILED, "Failed login attempt", getClientIp(httpRequest));
            });
            throw e;
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (!jwtService.isRefreshToken(token)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByEmailAndActiveTrue(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new BadRequestException("Expired or invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        logAudit(user, AuditAction.TOKEN_REFRESH, "Token refreshed successfully", null);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(token)
            .expiresIn(jwtService.getAccessTokenExpiration())
            .user(mapToUserProfile(user))
            .build();
    }

    @Override
    public void logout(String authHeader, HttpServletRequest request) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            userRepository.findByEmail(username).ifPresent(user -> 
                logAudit(user, AuditAction.LOGOUT, "User logged out", getClientIp(request))
            );
        }
    }

    @Override
    public UserProfileResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmailAndActiveTrue(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserProfile(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User with email not found"));

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // In a real application, send this token via email
        log.info("Password reset token for {}: {}", user.getEmail(), token);
        logAudit(user, AuditAction.PASSWORD_RESET, "Password reset requested", null);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
            .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (user.getPasswordResetExpiry() == null || user.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);

        logAudit(user, AuditAction.PASSWORD_CHANGED, "Password reset successfully via token", null);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmailAndActiveTrue(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        logAudit(user, AuditAction.PASSWORD_CHANGED, "Password changed by user", null);
    }

    private UserProfileResponse mapToUserProfile(User user) {
        return UserProfileResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .role(user.getRole().name())
            .profileImage(user.getProfileImage())
            .lastLogin(user.getLastLogin())
            .build();
    }

    private void logAudit(User user, AuditAction action, String description, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
            .user(user)
            .action(action)
            .description(description)
            .ipAddress(ipAddress)
            .success(true)
            .build();
        auditLogRepository.save(auditLog);
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
