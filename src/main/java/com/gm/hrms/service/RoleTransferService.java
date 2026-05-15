package com.gm.hrms.service;

import com.gm.hrms.dto.request.RoleTransferRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.RoleTransferResponseDTO;
import org.springframework.data.domain.Pageable;

public interface RoleTransferService {

    RoleTransferResponseDTO initiateTransfer(RoleTransferRequestDTO dto);

    PageResponseDTO<RoleTransferResponseDTO> listTransfers(Pageable pageable);

    RoleTransferResponseDTO cancelTransfer(Long id);
}