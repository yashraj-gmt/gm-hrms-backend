package com.gm.hrms.service.impl;

import com.gm.hrms.config.CustomUserDetails;
import com.gm.hrms.dto.request.AssignUsersRequestDTO;
import com.gm.hrms.dto.request.RolePermissionDTO;
import com.gm.hrms.dto.request.SavePermissionsRequestDTO;
import com.gm.hrms.dto.response.AssignedUserResponseDTO;
import com.gm.hrms.dto.response.MyPermissionsResponseDTO;
import com.gm.hrms.dto.response.PermissionsMatrixResponseDTO;
import com.gm.hrms.dto.response.RolePermissionResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.ModuleType;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.RolesPermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesPermissionsServiceImpl implements RolesPermissionsService {

    private static final Map<ModuleType, String> MODULE_LABELS = Map.of(
            ModuleType.DASHBOARD,         "Dashboard",
            ModuleType.EMPLOYEES,         "Employees",
            ModuleType.ATTENDANCE,        "Attendance",
            ModuleType.LEAVE_MANAGEMENT,  "Leave Management",
            ModuleType.TIMESHEET,         "Timesheet",
            ModuleType.ROLES_PERMISSION,  "Roles & Permission",
            ModuleType.PAYROLL,           "Payroll",
            ModuleType.PROJECTS,          "Projects",
            ModuleType.REPORT,            "Report"
    );

    private final RolePermissionRepository       permRepo;
    private final RoleUserAssignmentRepository   assignRepo;
    private final PersonalInformationRepository  personRepo;
    private final UserAuthRepository             userAuthRepo;

    // ── Get Permissions Matrix ────────────────────────────────────────────────

    @Override
    public PermissionsMatrixResponseDTO getPermissions(RoleType roleType) {

        List<RolePermission> existing = permRepo.findAllByRoleType(roleType);

        // Build a map for quick lookup
        Map<ModuleType, RolePermission> permMap = existing.stream()
                .collect(Collectors.toMap(RolePermission::getModule, p -> p));

        // Return ALL modules (with defaults for missing rows)
        List<RolePermissionResponseDTO> list = Arrays.stream(ModuleType.values())
                .map(mod -> {
                    RolePermission p = permMap.get(mod);
                    if (p == null) {
                        return RolePermissionResponseDTO.builder()
                                .module(mod)
                                .moduleLabel(MODULE_LABELS.get(mod))
                                .canAll(false).canView(false)
                                .canCreate(false).canEdit(false).canDelete(false)
                                .build();
                    }
                    return toResponseDTO(p);
                })
                .collect(Collectors.toList());

        int userCount = assignRepo.countByRoleTypeAndActiveTrue(roleType);

        return PermissionsMatrixResponseDTO.builder()
                .roleType(roleType)
                .roleName(toRoleName(roleType))
                .assignedUserCount(userCount)
                .permissions(list)
                .build();
    }

    // ── Save Permissions ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public PermissionsMatrixResponseDTO savePermissions(SavePermissionsRequestDTO dto) {

        String updatedBy = currentUsername();

        for (RolePermissionDTO req : dto.getPermissions()) {

            boolean all    = Boolean.TRUE.equals(req.getCanAll());
            boolean view   = all || Boolean.TRUE.equals(req.getCanView());
            boolean create = all || Boolean.TRUE.equals(req.getCanCreate());
            boolean edit   = all || Boolean.TRUE.equals(req.getCanEdit());
            boolean delete = all || Boolean.TRUE.equals(req.getCanDelete());

            // Recompute canAll
            boolean computedAll = view && create && edit && delete;

            RolePermission perm = permRepo
                    .findByRoleTypeAndModule(dto.getRoleType(), req.getModule())
                    .orElse(RolePermission.builder()
                            .roleType(dto.getRoleType())
                            .module(req.getModule())
                            .build());

            perm.setCanView(view);
            perm.setCanCreate(create);
            perm.setCanEdit(edit);
            perm.setCanDelete(delete);
            perm.setCanAll(computedAll);
            perm.setUpdatedBy(updatedBy);
            perm.setUpdatedAt(LocalDateTime.now());

            permRepo.save(perm);
        }

        return getPermissions(dto.getRoleType());
    }

    // ── Assigned Users ────────────────────────────────────────────────────────

    @Override
    public List<AssignedUserResponseDTO> getAssignedUsers(RoleType roleType) {

        Set<Long> assignedIds = assignRepo.findAllByRoleTypeAndActiveTrue(roleType)
                .stream()
                .map(a -> a.getPersonalInformation().getId())
                .collect(Collectors.toSet());

        return personRepo.findAll().stream()
                .map(person -> AssignedUserResponseDTO.builder()
                        .personalInformationId(person.getId())
                        .fullName(person.getFirstName() + " " + person.getLastName())
                        .designation(
                                person.getWorkProfile() != null &&
                                        person.getWorkProfile().getDesignation() != null
                                        ? person.getWorkProfile().getDesignation().getName()
                                        : "—"
                        )
                        .department(
                                person.getWorkProfile() != null &&
                                        person.getWorkProfile().getDepartment() != null
                                        ? person.getWorkProfile().getDepartment().getName()
                                        : "—"
                        )
                        .avatarInitials(initials(person.getFirstName(), person.getLastName()))
                        .assigned(assignedIds.contains(person.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignUsers(AssignUsersRequestDTO dto) {

        String by = currentUsername();

        // Remove all existing assignments for role
        assignRepo.deleteAllByRoleType(dto.getRoleType());

        // Re-create with new list
        for (Long personId : dto.getAssignedPersonIds()) {
            PersonalInformation person = personRepo.findById(personId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Person not found: " + personId));

            assignRepo.save(RoleUserAssignment.builder()
                    .roleType(dto.getRoleType())
                    .personalInformation(person)
                    .active(true)
                    .assignedBy(by)
                    .build());
        }
    }

    // ── My Permissions ────────────────────────────────────────────────────────

    @Override
    public List<MyPermissionsResponseDTO> getMyPermissions() {

        String username = currentUsername();
        UserAuth auth = userAuthRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RoleType role = auth.getRole();
        List<RolePermission> perms = permRepo.findAllByRoleType(role);
        Map<ModuleType, RolePermission> permMap = perms.stream()
                .collect(Collectors.toMap(RolePermission::getModule, p -> p));

        return Arrays.stream(ModuleType.values())
                .map(mod -> {
                    RolePermission p = permMap.get(mod);
                    return MyPermissionsResponseDTO.builder()
                            .module(mod)
                            .moduleLabel(MODULE_LABELS.get(mod))
                            .canView(p != null && Boolean.TRUE.equals(p.getCanView()))
                            .canCreate(p != null && Boolean.TRUE.equals(p.getCanCreate()))
                            .canEdit(p != null && Boolean.TRUE.equals(p.getCanEdit()))
                            .canDelete(p != null && Boolean.TRUE.equals(p.getCanDelete()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RolePermissionResponseDTO toResponseDTO(RolePermission p) {
        return RolePermissionResponseDTO.builder()
                .module(p.getModule())
                .moduleLabel(MODULE_LABELS.get(p.getModule()))
                .canAll(p.getCanAll())
                .canView(p.getCanView())
                .canCreate(p.getCanCreate())
                .canEdit(p.getCanEdit())
                .canDelete(p.getCanDelete())
                .build();
    }

    private String initials(String first, String last) {
        return ((first != null && !first.isEmpty() ? String.valueOf(first.charAt(0)) : "") +
                (last  != null && !last.isEmpty()  ? String.valueOf(last.charAt(0))  : "")).toUpperCase();
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    private String toRoleName(RoleType rt) {
        return switch (rt) {
            case ADMIN    -> "Administrator";
            case HR       -> "Human Resources";
            case EMPLOYEE -> "Employee";
            case INTERN   -> "Intern";
            case TRAINEE  -> "Trainee";
        };
    }
}