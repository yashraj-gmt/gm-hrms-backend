package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveBalanceFilterDTO;
import com.gm.hrms.dto.response.LeaveBalanceResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface LeaveBalanceQueryService {

    PageResponseDTO<LeaveBalanceResponseDTO> getAll(
            LeaveBalanceFilterDTO filter,
            Pageable pageable
    );
}