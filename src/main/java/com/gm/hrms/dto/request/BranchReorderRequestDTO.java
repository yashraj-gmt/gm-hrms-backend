package com.gm.hrms.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class BranchReorderRequestDTO {
    private List<BranchReorderItemDTO> items;
}