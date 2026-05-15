package com.gm.hrms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BranchResponseDTO {

    private Long               id;
    private String             branchName;
    private String             branchCode;
    private Boolean            active;
    private Long               parentId;
    private Integer            sortOrder;
    private AddressResponseDTO address;
    private List<BranchResponseDTO> children;
    private LocalDateTime      createdAt;
    private LocalDateTime      updatedAt;
}