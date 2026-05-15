package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RoleType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PermissionsMatrixResponseDTO {
    private RoleType                        roleType;
    private String                          roleName;
    private int                             assignedUserCount;
    private List<RolePermissionResponseDTO> permissions;
}