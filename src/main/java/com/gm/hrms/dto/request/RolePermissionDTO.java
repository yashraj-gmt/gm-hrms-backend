package com.gm.hrms.dto.request;

import com.gm.hrms.enums.ModuleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RolePermissionDTO {

    @NotNull
    private ModuleType module;

    private Boolean canView   = false;
    private Boolean canCreate = false;
    private Boolean canEdit   = false;
    private Boolean canDelete = false;
    private Boolean canAll    = false;
}