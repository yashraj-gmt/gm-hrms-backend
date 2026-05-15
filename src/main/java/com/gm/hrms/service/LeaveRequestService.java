package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.dto.response.LeaveRequestResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeaveRequestService {

    // 🔥 APPLY LEAVE
    LeaveRequestResponseDTO apply(LeaveRequestDTO dto);

    // 🔥 APPROVAL FLOW
    void approve(Long leaveRequestId, Long approverId);

    void reject(Long leaveRequestId, String reason);

    // 🔥 CANCEL
    void cancel(Long leaveRequestId);

    // 🔥 GET
    PageResponseDTO<LeaveRequestResponseDTO> getMyLeaves(
            Long personalId,
            Pageable pageable);

    PageResponseDTO<LeaveRequestResponseDTO> getAll(Pageable pageable);

    void requestDocument(Long leaveRequestId);
}