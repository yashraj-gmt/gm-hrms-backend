package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SavePermissionsRequestDTO {

    @NotNull(message = "roleType is required")
    private RoleType roleType;

    @NotEmpty(message = "permissions list must not be empty")
    @Valid
    private List<RolePermissionDTO> permissions;
}