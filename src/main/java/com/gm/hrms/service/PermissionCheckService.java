package com.gm.hrms.service;

import com.gm.hrms.enums.ModuleType;

public interface PermissionCheckService {

    boolean hasPermission(ModuleType module, String action); // action: "view","create","edit","delete"

    void assertPermission(ModuleType module, String action); // throws AccessDeniedException if not allowed
}