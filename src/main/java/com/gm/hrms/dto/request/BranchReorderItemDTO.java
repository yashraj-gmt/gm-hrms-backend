package com.gm.hrms.dto.request;

import lombok.Data;

@Data
public class BranchReorderItemDTO {
    private Long    id;
    private Long    parentId;    // null = root
    private Integer sortOrder;
}