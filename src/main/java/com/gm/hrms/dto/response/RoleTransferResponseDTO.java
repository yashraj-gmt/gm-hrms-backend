package com.gm.hrms.dto.response;

import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.TransferStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RoleTransferResponseDTO {
    private Long           id;
    private RoleType       sourceRole;
    private String         recipientName;
    private String         recipientDesignation;
    private LocalDate      startDate;
    private LocalDate      endDate;
    private Boolean        isPermanent;
    private String         reason;
    private TransferStatus status;
    private String         initiatedBy;
    private LocalDateTime  confirmedAt;
    private String         duration;   // e.g. "4 Weeks"
}