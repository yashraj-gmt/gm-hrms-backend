package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.TransferOtpPurpose;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolesPermissionsController {

    private final RolesPermissionsService   rolesService;
    private final TransferOtpService        otpService;
    private final RoleTransferService       roleTransferService;
    private final DesignationTransferService designationTransferService;

    // ── Permission Matrix ─────────────────────────────────────────────────────

    /**
     * GET /api/roles/permissions?roleType=HR
     * Returns the full permission matrix for a role.
     * HR can view their own; ADMIN can view any role.
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<PermissionsMatrixResponseDTO>> getPermissions(
            @RequestParam(defaultValue = "HR") RoleType roleType) {

        return ok("Permissions fetched", rolesService.getPermissions(roleType));
    }

    /**
     * PUT /api/roles/permissions
     * Saves the full permission matrix (upsert per module row).
     * ADMIN only.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/permissions")
    @Auditable(action = AuditAction.UPDATE, resource = "RolePermissions",
            description = "Update role permission matrix")
    public ResponseEntity<ApiResponse<PermissionsMatrixResponseDTO>> savePermissions(
            @Valid @RequestBody SavePermissionsRequestDTO dto) {

        return ok("Permissions saved successfully", rolesService.savePermissions(dto));
    }

    // ── Assigned Users ────────────────────────────────────────────────────────

    /**
     * GET /api/roles/assigned-users?roleType=HR
     * Returns all persons with their assignment flag for the given role.
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/assigned-users")
    public ResponseEntity<ApiResponse<List<AssignedUserResponseDTO>>> getAssignedUsers(
            @RequestParam(defaultValue = "HR") RoleType roleType) {

        return ok("Assigned users fetched", rolesService.getAssignedUsers(roleType));
    }

    /**
     * POST /api/roles/assign-users
     * Replaces the assignment list for a role entirely.
     * ADMIN only.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign-users")
    @Auditable(action = AuditAction.UPDATE, resource = "RoleUserAssignment",
            description = "Assign users to role")
    public ResponseEntity<ApiResponse<Void>> assignUsers(
            @Valid @RequestBody AssignUsersRequestDTO dto) {

        rolesService.assignUsers(dto);
        return ok("User assignments updated successfully", null);
    }

    // ── OTP for Transfers ─────────────────────────────────────────────────────

    /**
     * POST /api/roles/transfer/otp/generate
     * Generates and emails a 6-digit OTP to the requesting HR/Admin user.
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/transfer/otp/generate")
    public ResponseEntity<ApiResponse<Void>> generateOtp(
            @Valid @RequestBody GenerateTransferOtpRequestDTO dto,
            @AuthenticationPrincipal UserDetails principal) {

        otpService.generateOtp(principal.getUsername(), dto.getPurpose());
        return ok("OTP sent to your registered office email. Valid for 5 minutes.", null);
    }

    /**
     * POST /api/roles/transfer/otp/verify
     * Verifies the OTP (does NOT consume it — consumption happens on confirm).
     */
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/transfer/otp/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyTransferOtpRequestDTO dto,
            @AuthenticationPrincipal UserDetails principal) {

        otpService.verifyOtp(principal.getUsername(), dto.getOtp(), dto.getPurpose());
        return ok("OTP verified successfully. You may now confirm the transfer.", null);
    }

    // ── Role Transfer ─────────────────────────────────────────────────────────

    /**
     * POST /api/roles/transfer/role
     * Initiates + confirms a Role Transfer (OTP is re-verified inside service).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/transfer/role")
    @Auditable(action = AuditAction.UPDATE, resource = "RoleTransfer",
            description = "Confirm role transfer")
    public ResponseEntity<ApiResponse<RoleTransferResponseDTO>> initiateRoleTransfer(
            @Valid @RequestBody RoleTransferRequestDTO dto) {

        return ok("Role transfer confirmed successfully",
                roleTransferService.initiateTransfer(dto));
    }

    /**
     * GET /api/roles/transfer/role?page=0&size=10
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transfer/role")
    public ResponseEntity<ApiResponse<PageResponseDTO<RoleTransferResponseDTO>>> listRoleTransfers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ok("Role transfers fetched",
                roleTransferService.listTransfers(
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    /**
     * PATCH /api/roles/transfer/role/{id}/cancel
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/transfer/role/{id}/cancel")
    @Auditable(action = AuditAction.UPDATE, resource = "RoleTransfer",
            description = "Cancel role transfer")
    public ResponseEntity<ApiResponse<RoleTransferResponseDTO>> cancelRoleTransfer(
            @PathVariable Long id) {

        return ok("Role transfer cancelled", roleTransferService.cancelTransfer(id));
    }

    // ── Designation Transfer ──────────────────────────────────────────────────

    /**
     * POST /api/roles/transfer/designation
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/transfer/designation")
    @Auditable(action = AuditAction.UPDATE, resource = "DesignationTransfer",
            description = "Confirm designation transfer")
    public ResponseEntity<ApiResponse<DesignationTransferResponseDTO>> initiateDesignationTransfer(
            @Valid @RequestBody DesignationTransferRequestDTO dto) {

        return ok("Designation transfer confirmed successfully",
                designationTransferService.initiateTransfer(dto));
    }

    /**
     * GET /api/roles/transfer/designation?page=0&size=10
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transfer/designation")
    public ResponseEntity<ApiResponse<PageResponseDTO<DesignationTransferResponseDTO>>> listDesignationTransfers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ok("Designation transfers fetched",
                designationTransferService.listTransfers(
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    /**
     * PATCH /api/roles/transfer/designation/{id}/cancel
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/transfer/designation/{id}/cancel")
    @Auditable(action = AuditAction.UPDATE, resource = "DesignationTransfer",
            description = "Cancel designation transfer")
    public ResponseEntity<ApiResponse<DesignationTransferResponseDTO>> cancelDesignationTransfer(
            @PathVariable Long id) {

        return ok("Designation transfer cancelled",
                designationTransferService.cancelTransfer(id));
    }

    // ── My Permissions ────────────────────────────────────────────────────────

    /**
     * GET /api/roles/my-permissions
     * Returns the permission set for the currently authenticated user's role.
     * Available to all authenticated roles.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-permissions")
    public ResponseEntity<ApiResponse<List<MyPermissionsResponseDTO>>> getMyPermissions() {
        return ok("Your permissions fetched", rolesService.getMyPermissions());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private <T> ResponseEntity<ApiResponse<T>> ok(String msg, T data) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .success(true).message(msg).data(data).build());
    }
}