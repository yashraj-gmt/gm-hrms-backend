package com.gm.hrms.service;

import com.gm.hrms.dto.request.AssignUsersRequestDTO;
import com.gm.hrms.dto.request.SavePermissionsRequestDTO;
import com.gm.hrms.dto.response.AssignedUserResponseDTO;
import com.gm.hrms.dto.response.MyPermissionsResponseDTO;
import com.gm.hrms.dto.response.PermissionsMatrixResponseDTO;
import com.gm.hrms.enums.RoleType;

import java.util.List;

public interface RolesPermissionsService {

    /** Returns the full permission matrix for a given role */
    PermissionsMatrixResponseDTO getPermissions(RoleType roleType);

    /** Saves (upsert) the full permission matrix for a role */
    PermissionsMatrixResponseDTO savePermissions(SavePermissionsRequestDTO dto);

    /** Returns all persons with assignment flag for the given role */
    List<AssignedUserResponseDTO> getAssignedUsers(RoleType roleType);

    /** Replaces assignment list entirely */
    void assignUsers(AssignUsersRequestDTO dto);

    /** Returns permissions of the currently authenticated user */
    List<MyPermissionsResponseDTO> getMyPermissions();
}