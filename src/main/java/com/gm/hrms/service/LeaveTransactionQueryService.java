package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveTransactionFilterDTO;
import com.gm.hrms.dto.response.LeaveTransactionResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface LeaveTransactionQueryService {

    PageResponseDTO<LeaveTransactionResponseDTO> getAll(
            LeaveTransactionFilterDTO filter,
            Pageable pageable
    );
}