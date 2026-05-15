package com.gm.hrms.mapper;

import com.gm.hrms.dto.request.DepartmentRequestDTO;
import com.gm.hrms.dto.request.SubDepartmentRequestDTO;
import com.gm.hrms.dto.response.DepartmentResponseDTO;
import com.gm.hrms.dto.response.SubDepartmentResponseDTO;
import com.gm.hrms.entity.Department;
import com.gm.hrms.entity.SubDepartment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DepartmentMapper {

    // CREATE
    public static Department toEntity(DepartmentRequestDTO dto) {
        Department dept = new Department();
        dept.setName(dto.getName());
        dept.setCode(dto.getCode());
        dept.setDescription(dto.getDescription());
        dept.setStatus(dto.getStatus());
        dept.setDeleted(false);

        if (dto.getSubDepartments() != null) {
            // Fix: if parent is Inactive, force all sub-depts Inactive too
            boolean parentActive = Boolean.TRUE.equals(dto.getStatus());
            for (SubDepartmentRequestDTO sdto : dto.getSubDepartments()) {
                SubDepartment sub = new SubDepartment();
                sub.setName(sdto.getName());
                sub.setCode(sdto.getCode());
                sub.setDescription(sdto.getDescription());
                sub.setStatus(parentActive ? sdto.getStatus() : false); // ← status sync
                sub.setDeleted(false);
                sub.setDepartment(dept);
                dept.getSubDepartments().add(sub);
            }
        }
        return dept;
    }

    // ── UPDATE (patch) ────────────────────────────────────────────────────────
    public static void patchUpdate(Department dept, DepartmentRequestDTO dto) {
        if (dto.getName()        != null) dept.setName(dto.getName());
        if (dto.getCode()        != null) dept.setCode(dto.getCode());
        if (dto.getDescription() != null) dept.setDescription(dto.getDescription());

        // Detect if we are switching parent to Inactive
        boolean deactivatingParent = dto.getStatus() != null
                && !dto.getStatus()
                && Boolean.TRUE.equals(dept.getStatus());

        if (dto.getStatus() != null) dept.setStatus(dto.getStatus());

        // ── Merge sub-departments ─────────────────────────────────────────────
        if (dto.getSubDepartments() != null) {
            // Index existing sub-depts by their DB id
            Map<Long, SubDepartment> existingById = dept.getSubDepartments().stream()
                    .filter(s -> s.getId() != null)
                    .collect(Collectors.toMap(SubDepartment::getId, s -> s));

            List<SubDepartment> merged = new ArrayList<>();
            boolean parentIsActive = Boolean.TRUE.equals(dept.getStatus());

            for (SubDepartmentRequestDTO sdto : dto.getSubDepartments()) {
                SubDepartment sub;
                if (sdto.getId() != null && existingById.containsKey(sdto.getId())) {
                    sub = existingById.get(sdto.getId()); // update existing
                } else {
                    sub = new SubDepartment();             // new sub-dept
                    sub.setDepartment(dept);
                    sub.setDeleted(false);
                }
                sub.setName(sdto.getName());
                sub.setCode(sdto.getCode());
                sub.setDescription(sdto.getDescription());
                // ← Status sync: if parent is Inactive, sub must also be Inactive
                sub.setStatus(parentIsActive ? sdto.getStatus() : false);
                merged.add(sub);
            }

            dept.getSubDepartments().clear();
            dept.getSubDepartments().addAll(merged);

        } else if (deactivatingParent) {
            // No sub-dept list provided but parent was deactivated → cascade
            dept.getSubDepartments().forEach(s -> s.setStatus(false));
        }
    }

    // ── RESPONSE mappers ──────────────────────────────────────────────────────
    public static DepartmentResponseDTO toResponse(Department dept) {
        List<SubDepartmentResponseDTO> subDtos = dept.getSubDepartments() == null
                ? List.of()
                : dept.getSubDepartments().stream()
                .map(DepartmentMapper::toSubResponse)
                .toList();

        return DepartmentResponseDTO.builder()
                .id(dept.getId())
                .name(dept.getName())
                .code(dept.getCode())
                .description(dept.getDescription())
                .status(dept.getStatus())
                .subDepartments(subDtos)
                .build();
    }

    private static SubDepartmentResponseDTO toSubResponse(SubDepartment sub) {
        return SubDepartmentResponseDTO.builder()
                .id(sub.getId())
                .name(sub.getName())
                .code(sub.getCode())
                .description(sub.getDescription())
                .status(sub.getStatus())
                .build();
    }
}