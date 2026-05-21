package com.gm.hrms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.ProfileUpdateRequestDTO;
import com.gm.hrms.dto.response.UserProfileResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ObjectMapper mapper;

    // ================= CREATE =================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Auditable(
            action      = AuditAction.CREATE_USER,
            resource    = "User",
            description = "Create full user profile (person + role + auth credentials)"
    )
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestParam("personalInformation") String personalInformationJson,
            @RequestParam(value = "intern",    required = false) String internJson,
            @RequestParam(value = "employee",  required = false) String employeeJson,
            @RequestParam(value = "trainee",   required = false) String traineeJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(required = false) Map<String, MultipartFile> documents,
            @RequestParam(value = "reasons", required = false) String reasonsJson   // ← String, not Map
    ) throws Exception {

        Map<String, String> reasons = null;
        if (reasonsJson != null && !reasonsJson.isBlank()) {
            try {
                reasons = mapper.readValue(reasonsJson,
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            } catch (Exception ignored) {
                reasons = null;
            }
        }

        Object response = userService.create(
                personalInformationJson,
                internJson, employeeJson, traineeJson,
                profileImage, documents, reasons
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("User created successfully")
                        .data(response)
                        .build()
        );
    }

    // ================= ME =================

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> getMe(
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponseDTO>builder()
                        .success(true)
                        .message("Profile fetched")
                        .data(userService.getMe(principal.getUsername()))
                        .build()
        );
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateMe(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody ProfileUpdateRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponseDTO>builder()
                        .success(true)
                        .message("Profile updated")
                        .data(userService.updateMe(principal.getUsername(), dto))
                        .build()
        );
    }

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponseDTO>> updateAvatar(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam("profileImage") MultipartFile profileImage) throws Exception {

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponseDTO>builder()
                        .success(true)
                        .message("Avatar updated")
                        .data(userService.updateAvatar(principal.getUsername(), profileImage))
                        .build()
        );
    }

    // ================= UNIQUENESS CHECKS (used by AddEmployee form) =================

    /**
     * GET /api/users/check-email?email=xxx&type=OFFICE|PERSONAL
     * Returns { available: true } if the email is not yet taken, { available: false } otherwise.
     * Used for real-time uniqueness validation in the Add Employee / Add Intern / Add Trainee forms.
     */
    @GetMapping("/check-email")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "OFFICE") String type) {

        boolean available = userService.isEmailAvailable(email, type);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Boolean>>builder()
                        .success(true)
                        .message("Email availability checked")
                        .data(Map.of("available", available))
                        .build()
        );
    }

    /**
     * GET /api/users/check-employee-code?code=GMEP001
     * Returns { available: true } if the employee code is not yet taken.
     */
    @GetMapping("/check-employee-code")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmployeeCode(
            @RequestParam String code) {

        boolean available = userService.isEmployeeCodeAvailable(code);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Boolean>>builder()
                        .success(true)
                        .message("Employee code availability checked")
                        .data(Map.of("available", available))
                        .build()
        );
    }
}