package com.gm.hrms.dto.request;

import com.gm.hrms.dto.request.AddressRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequestDTO {

    @NotBlank(message = "Branch name is required")
    private String branchName;

    @NotBlank(message = "Branch code is required")
    private String branchCode;

    private Boolean  active   = true;
    private Long     parentId;
    private Integer  sortOrder = 0;
    private AddressRequestDTO address;
}