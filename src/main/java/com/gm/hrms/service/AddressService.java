package com.gm.hrms.service;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Address;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {

    AddressResponseDTO create(AddressRequestDTO dto);

    AddressResponseDTO update(Long id, AddressRequestDTO dto);

    AddressResponseDTO getById(Long id);

    PageResponseDTO<AddressResponseDTO> getAll(Pageable pageable);
    void delete(Long id);
    void validateForSubmit(Address existing, AddressRequestDTO dto);
}