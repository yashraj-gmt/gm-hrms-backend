package com.gm.hrms.dto.request;

import com.gm.hrms.enums.RoleType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignUsersRequestDTO {

    @NotNull
    private RoleType roleType;

    /** IDs of PersonalInformation to assign (replaces entire list) */
    @NotEmpty(message = "assignedPersonIds must not be empty")
    private List<Long> assignedPersonIds;
}