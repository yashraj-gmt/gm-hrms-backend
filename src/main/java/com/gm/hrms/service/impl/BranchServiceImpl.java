package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.Address;
import com.gm.hrms.entity.Branch;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.mapper.BranchMapper;
import com.gm.hrms.repository.AddressRepository;
import com.gm.hrms.repository.BranchRepository;
import com.gm.hrms.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final AddressRepository addressRepository;

    // ── CREATE ───────────────────────────────────────────────────────────────
    @Override
    public BranchResponseDTO create(BranchRequestDTO dto) {

        if (branchRepository.existsByBranchCode(dto.getBranchCode())) {
            throw new DuplicateResourceException(
                    "Branch code already exists: " + dto.getBranchCode());
        }

        Branch branch = BranchMapper.toEntity(dto, null);

        if (dto.getParentId() != null) {
            Branch parent = branchRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent branch not found"));
            branch.setParentBranch(parent);
        }

        if (dto.getAddress() != null) {
            Address address = AddressMapper.toEntity(dto.getAddress());
            addressRepository.save(address);
            branch.setAddress(address);
        }

        branchRepository.save(branch);
        return BranchMapper.toResponse(branch);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    @Override
    public BranchResponseDTO update(Long id, BranchUpdateDTO dto) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        if (dto.getBranchCode() != null
                && branchRepository.existsByBranchCodeAndIdNot(dto.getBranchCode(), id)) {
            throw new DuplicateResourceException(
                    "Branch code already in use: " + dto.getBranchCode());
        }

        if (dto.getParentId() != null) {
            Branch parent = branchRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent branch not found"));
            branch.setParentBranch(parent);
        }

        Address address = branch.getAddress();
        if (dto.getAddress() != null) {
            if (address == null) {
                address = AddressMapper.toEntity(dto.getAddress());
                addressRepository.save(address);
            } else {
                AddressMapper.patchEntity(address, dto.getAddress());
            }
        }

        boolean wasActive = Boolean.TRUE.equals(branch.getActive());

        BranchMapper.patchEntity(branch, dto, address);
        branchRepository.save(branch);
        boolean isNowInactive = Boolean.FALSE.equals(branch.getActive());
        if (wasActive && isNowInactive) {
            cascadeInactiveToChildren(branch);
        }

        return BranchMapper.toResponse(branch);
    }


    private void cascadeInactiveToChildren(Branch branch) {
        for (Branch child : branch.getChildren()) {
            if (Boolean.TRUE.equals(child.getActive())) {
                child.setActive(false);
                branchRepository.save(child);
            }
            cascadeInactiveToChildren(child);
        }
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public BranchResponseDTO getById(Long id) {
        return BranchMapper.toResponse(
                branchRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Branch not found"))
        );
    }

    // ── GET ALL (paginated) ───────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BranchResponseDTO> getAll(Pageable pageable) {
        Page<Branch> page = branchRepository.findAll(pageable);
        List<BranchResponseDTO> content = page.getContent().stream()
                .map(BranchMapper::toResponse)
                .toList();
        return PageResponseDTO.<BranchResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ── GET TREE ──────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<BranchResponseDTO> getTree() {
        return branchRepository.findRootBranches().stream()
                .map(BranchMapper::toTreeResponse)
                .collect(Collectors.toList());
    }

    // ── REORDER ───────────────────────────────────────────────────────────────
    @Override
    public void reorder(BranchReorderRequestDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) return;

        Map<Long, Branch> branchMap = branchRepository.findAllById(
                dto.getItems().stream()
                        .map(BranchReorderItemDTO::getId)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Branch::getId, b -> b));

        for (BranchReorderItemDTO item : dto.getItems()) {
            Branch branch = branchMap.get(item.getId());
            if (branch == null) continue;

            if (item.getSortOrder() != null) {
                branch.setSortOrder(item.getSortOrder());
            }

            if (item.getParentId() == null) {
                branch.setParentBranch(null);
            } else if (!item.getParentId().equals(
                    branch.getParentBranch() != null
                            ? branch.getParentBranch().getId()
                            : null)) {
                Branch newParent = branchMap.getOrDefault(
                        item.getParentId(),
                        branchRepository.findById(item.getParentId()).orElse(null));
                branch.setParentBranch(newParent);
            }
        }

        branchRepository.saveAll(branchMap.values());
    }

    // ── DELETE
    @Override
    public void delete(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        softDeleteRecursive(branch);
        branchRepository.save(branch);
    }

    private void softDeleteRecursive(Branch branch) {
        branch.setDeleted(true);
        branch.setDeletedAt(LocalDateTime.now());

        for (Branch child : branch.getChildren()) {
            softDeleteRecursive(child);
        }
    }
}