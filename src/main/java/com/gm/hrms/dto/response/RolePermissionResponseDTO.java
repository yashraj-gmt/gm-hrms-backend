package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ModuleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RolePermissionResponseDTO {
    private ModuleType module;
    private String     moduleLabel;   // human-readable
    private Boolean    canAll;
    private Boolean    canView;
    private Boolean    canCreate;
    private Boolean    canEdit;
    private Boolean    canDelete;
}