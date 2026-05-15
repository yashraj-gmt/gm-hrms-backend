package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.BranchRequestDTO;
import com.gm.hrms.dto.request.BranchUpdateDTO;
import com.gm.hrms.dto.response.BranchResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.entity.Branch;

import java.util.List;
import java.util.stream.Collectors;

public class BranchMapper {

    private BranchMapper() {}

    public static Branch toEntity(BranchRequestDTO dto, Address address) {
        Branch b = new Branch();
        b.setBranchName(dto.getBranchName());
        b.setBranchCode(dto.getBranchCode());
        b.setActive(dto.getActive() != null ? dto.getActive() : true);
        b.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        b.setAddress(address);
        return b;
    }

    public static void patchEntity(Branch branch, BranchUpdateDTO dto, Address address) {
        if (dto.getBranchName() != null) branch.setBranchName(dto.getBranchName());
        if (dto.getBranchCode() != null) branch.setBranchCode(dto.getBranchCode());
        if (dto.getActive()     != null) branch.setActive(dto.getActive());
        if (dto.getSortOrder()  != null) branch.setSortOrder(dto.getSortOrder());
        if (address             != null) branch.setAddress(address);
    }

    /** Flat response — used for create/update/getById. No children loaded. */
    public static BranchResponseDTO toResponse(Branch branch) {
        return BranchResponseDTO.builder()
                .id(branch.getId())
                .branchName(branch.getBranchName())
                .branchCode(branch.getBranchCode())
                .active(branch.getActive())
                .sortOrder(branch.getSortOrder())
                .parentId(branch.getParentBranch() != null ? branch.getParentBranch().getId() : null)
                .address(AddressMapper.toResponse(branch.getAddress()))
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }

    /** Recursive tree response — used for /tree endpoint. */
    public static BranchResponseDTO toTreeResponse(Branch branch) {
        List<BranchResponseDTO> childDTOs = branch.getChildren().stream()
                .map(BranchMapper::toTreeResponse)
                .collect(Collectors.toList());

        return BranchResponseDTO.builder()
                .id(branch.getId())
                .branchName(branch.getBranchName())
                .branchCode(branch.getBranchCode())
                .active(branch.getActive())
                .sortOrder(branch.getSortOrder())
                .parentId(branch.getParentBranch() != null ? branch.getParentBranch().getId() : null)
                .address(AddressMapper.toResponse(branch.getAddress()))
                .children(childDTOs)
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}