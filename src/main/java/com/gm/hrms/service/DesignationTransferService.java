package com.gm.hrms.service;

import com.gm.hrms.dto.request.DesignationTransferRequestDTO;
import com.gm.hrms.dto.response.DesignationTransferResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface DesignationTransferService {

    DesignationTransferResponseDTO initiateTransfer(DesignationTransferRequestDTO dto);

    PageResponseDTO<DesignationTransferResponseDTO> listTransfers(Pageable pageable);

    DesignationTransferResponseDTO cancelTransfer(Long id);
}