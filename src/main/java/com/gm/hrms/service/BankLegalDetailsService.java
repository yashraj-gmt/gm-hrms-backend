package com.gm.hrms.service;

import com.gm.hrms.dto.request.BankLegalDetailsRequestDTO;
import com.gm.hrms.dto.response.BankLegalDetailsResponseDTO;

public interface BankLegalDetailsService {

    BankLegalDetailsResponseDTO saveOrUpdate(
            Long personalInformationId,
            BankLegalDetailsRequestDTO requestDTO);

    BankLegalDetailsResponseDTO getMyDetails(Long personalInformationId);
}