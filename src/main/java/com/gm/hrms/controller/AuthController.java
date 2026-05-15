package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.AuthService;
import com.gm.hrms.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService  otpService;

    // ═══════════════════════════ LOGIN ═══════════════════════════════════════

    @PostMapping("/login")
    @Auditable(
            action      = AuditAction.LOGIN,
            resource    = "Auth",
            description = "User login attempt"
    )
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDTO>builder()
                        .success(true)
                        .message("Login successful")
                        .data(authService.login(request))
                        .build()
        );
    }

    // ═══════════════════════ REFRESH TOKEN ═══════════════════════════════════

    @PostMapping("/refresh")
    @Auditable(
            action      = AuditAction.REFRESH_TOKEN,
            resource    = "Auth",
            description = "Access token refresh"
    )
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.<LoginResponseDTO>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(authService.refreshToken(request.getRefreshToken()))
                        .build()
        );
    }

    // ═══════════════════════════ LOGOUT ══════════════════════════════════════

    @PostMapping("/logout")
    @Auditable(
            action      = AuditAction.LOGOUT,
            resource    = "Auth",
            description = "User logout"
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequestDTO request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logged out successfully")
                        .build()
        );
    }

    // ══════════════════════ CHANGE PASSWORD ══════════════════════════════════

    @PostMapping("/change-password")
    @Auditable(
            action      = AuditAction.CHANGE_PASSWORD,
            resource    = "Auth",
            description = "Password change request"
    )
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        authService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password changed successfully")
                        .build()
        );
    }

    // ══════════════════════ FORGOT PASSWORD ══════════════════════════════════
    // Step 1 — send OTP to email

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {

        otpService.generateAndSend(request.getEmail());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("A 6-digit OTP has been sent to your email address. It expires in 5 minutes.")
                        .build()
        );
    }

    // ═══════════════════════ VERIFY OTP ══════════════════════════════════════
    // Step 2 — verify the code before allowing password reset

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequestDTO request) {

        otpService.verify(request.getEmail(), request.getOtp());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP verified successfully. You may now reset your password.")
                        .build()
        );
    }

    // ═══════════════════════ RESET PASSWORD ══════════════════════════════════
    // Step 3 — set the new password (OTP re-verified for security)

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {

        // Re-verify OTP to prevent direct API abuse
        otpService.verify(request.getEmail(), request.getOtp());

        authService.resetPassword(request);

        // Mark OTP as consumed so it cannot be replayed
        otpService.markUsed(request.getEmail(), request.getOtp());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password reset successfully. You can now log in with your new password.")
                        .build()
        );
    }
}