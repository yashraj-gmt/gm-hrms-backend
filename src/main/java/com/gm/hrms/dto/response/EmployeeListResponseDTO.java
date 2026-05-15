package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmployeeListResponseDTO {

    // ── Paginated content ──────────────────────────────────────────────────
    private List<EmployeeListItemDTO> content;
    private int     page;
    private int     size;
    private long    totalElements;   // matched rows for current filter
    private int     totalPages;
    private boolean first;
    private boolean last;

    // ── Global summary (never filtered) ───────────────────────────────────
    private long totalEmployees;   // all SUBMITTED (any type)
    private long activeCount;
    private long inactiveCount;
    private long onHoldCount;
    private long draftCount;

    // ── Type-specific submitted counts (for tab badges) ──────────────────
    private long employeeCount;
    private long internCount;
    private long traineeCount;
}