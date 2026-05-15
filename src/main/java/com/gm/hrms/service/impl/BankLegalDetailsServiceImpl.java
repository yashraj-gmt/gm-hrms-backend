package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.BankLegalDetailsRequestDTO;
import com.gm.hrms.dto.response.BankLegalDetailsResponseDTO;
import com.gm.hrms.entity.BankLegalDetails;
import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.BankLegalDetailsMapper;
import com.gm.hrms.repository.BankLegalDetailsRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.BankLegalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankLegalDetailsServiceImpl implements BankLegalDetailsService {

    private final BankLegalDetailsRepository repository;
    private final PersonalInformationRepository personalInformationRepository;

    @Override
    public BankLegalDetailsResponseDTO saveOrUpdate(
            Long personalInformationId,
            BankLegalDetailsRequestDTO dto) {

        PersonalInformation personalInformation = personalInformationRepository.findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        BankLegalDetails details = repository.findByPersonalInformationId(personalInformationId)
                .orElse(null);

        if (details == null) {
            details = BankLegalDetailsMapper.toEntity(dto, personalInformation);
        } else {
            BankLegalDetailsMapper.updateEntity(details, dto);
        }

        repository.save(details);

        return BankLegalDetailsMapper.toResponse(details);
    }

    @Override
    public BankLegalDetailsResponseDTO getMyDetails(Long personalInformationId) {

        BankLegalDetails details = repository.findByPersonalInformationId(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank details not found"));

        return BankLegalDetailsMapper.toResponse(details);
    }
}