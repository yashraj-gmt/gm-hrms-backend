package com.gm.hrms.controller;

import com.gm.hrms.audit.Auditable;
import com.gm.hrms.audit.AuditAction;
import com.gm.hrms.dto.request.LeaveTransactionFilterDTO;
import com.gm.hrms.dto.response.LeaveTransactionResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.payload.ApiResponse;
import com.gm.hrms.service.LeaveTransactionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-transactions")
@RequiredArgsConstructor
public class LeaveTransactionController {

    private final LeaveTransactionQueryService service;

    // ================= SEARCH =================
    @PostMapping("/search")
    @Auditable(
            action      = AuditAction.SEARCH_LEAVE_TRANSACTION,
            resource    = "LeaveTransaction",
            description = "Search leave transactions with filter"
    )
    public ResponseEntity<ApiResponse<PageResponseDTO<LeaveTransactionResponseDTO>>> search(
            @RequestBody(required = false) LeaveTransactionFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {


        PageResponseDTO<LeaveTransactionResponseDTO> response =
                service.getAll(filter, PageRequest.of(page, size));

        return ResponseEntity.ok(
                ApiResponse.<PageResponseDTO<LeaveTransactionResponseDTO>>builder()
                        .success(true)
                        .message("Transactions fetched successfully")
                        .data(response)
                        .build()
        );
    }
}