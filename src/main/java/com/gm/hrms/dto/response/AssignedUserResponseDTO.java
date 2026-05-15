package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignedUserResponseDTO {
    private Long    personalInformationId;
    private String  fullName;
    private String  designation;
    private String  department;
    private String  avatarInitials;
    private Boolean assigned;
}