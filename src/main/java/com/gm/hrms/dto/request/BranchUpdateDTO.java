package com.gm.hrms.dto.request;

import com.gm.hrms.dto.request.AddressRequestDTO;
import lombok.Data;

@Data
public class BranchUpdateDTO {

    private String  branchName;
    private String  branchCode;
    private Boolean active;
    private Long    parentId;
    private Integer sortOrder;
    private AddressRequestDTO address;
}