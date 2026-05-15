package com.gm.hrms.controller;

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
            @RequestParam(required = false) Map<String, String> reasons
    ) throws Exception {

        Object response = userService.create(
                personalInformationJson,
                internJson,
                employeeJson,
                traineeJson,
                profileImage,
                documents,
                reasons
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("User created successfully")
                        .data(response)
                        .build()
        );
    }

    // Add to UserController.java

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
}