package com.gm.hrms.service;

import com.gm.hrms.dto.request.PersonalInformationRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;

import java.util.List;

public interface PersonalInformationService {

    PersonalInformationResponseDTO create(PersonalInformationRequestDTO dto);

    PersonalInformationResponseDTO update(Long id, PersonalInformationRequestDTO dto);

    PersonalInformationResponseDTO getById(Long id);

    List<PersonalInformationResponseDTO> getAll();

    void delete(Long id);
}