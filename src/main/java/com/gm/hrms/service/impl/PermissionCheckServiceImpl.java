package com.gm.hrms.service.impl;

import com.gm.hrms.entity.RolePermission;
import com.gm.hrms.entity.UserAuth;
import com.gm.hrms.enums.ModuleType;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.RolePermissionRepository;
import com.gm.hrms.repository.UserAuthRepository;
import com.gm.hrms.service.PermissionCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionCheckServiceImpl implements PermissionCheckService {

    private final RolePermissionRepository permRepo;
    private final UserAuthRepository       userAuthRepo;

    @Override
    public boolean hasPermission(ModuleType module, String action) {
        try {
            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            UserAuth auth = userAuthRepo.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            RolePermission perm = permRepo
                    .findByRoleTypeAndModule(auth.getRole(), module)
                    .orElse(null);

            if (perm == null) return false;

            return switch (action.toLowerCase()) {
                case "view"   -> Boolean.TRUE.equals(perm.getCanView());
                case "create" -> Boolean.TRUE.equals(perm.getCanCreate());
                case "edit"   -> Boolean.TRUE.equals(perm.getCanEdit());
                case "delete" -> Boolean.TRUE.equals(perm.getCanDelete());
                default       -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void assertPermission(ModuleType module, String action) {
        if (!hasPermission(module, action)) {
            throw new AccessDeniedException(
                    "You do not have [" + action.toUpperCase() + "] permission on module: " + module);
        }
    }
}