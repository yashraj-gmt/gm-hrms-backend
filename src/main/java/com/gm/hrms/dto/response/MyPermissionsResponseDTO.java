package com.gm.hrms.dto.response;

import com.gm.hrms.enums.ModuleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyPermissionsResponseDTO {
    private ModuleType module;
    private String     moduleLabel;
    private Boolean    canView;
    private Boolean    canCreate;
    private Boolean    canEdit;
    private Boolean    canDelete;
}