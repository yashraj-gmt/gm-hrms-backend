package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveBalanceFilterDTO;
import com.gm.hrms.dto.response.LeaveBalanceResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveBalanceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-balance")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceQueryService service;

    // ================= SEARCH =================
    @PostMapping("/search")
    @Auditable(
            action      = AuditAction.SEARCH_LEAVE_BALANCE,
            resource    = "LeaveBalance",
            description = "Search leave balances with filter"
    )
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveBalanceResponseDTO>>> getAll(
            @RequestBody(required = false) LeaveBalanceFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponseDTO<LeaveBalanceResponseDTO> response =
                service.getAll(filter, PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveBalanceResponseDTO>>builder()
                        .success(true)
                        .message("Leave balance fetched successfully")
                        .data(response)
                        .build()
        );
    }
}