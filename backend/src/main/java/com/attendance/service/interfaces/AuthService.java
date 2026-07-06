package com.attendance.service.interfaces;

import com.attendance.dto.auth.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String authHeader, HttpServletRequest request);
    UserProfileResponse getCurrentUser();
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void changePassword(ChangePasswordRequest request);
}
