package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.AddressRequestDTO;
import com.gm.hrms.dto.response.AddressResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.Address;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.repository.AddressRepository;
import com.gm.hrms.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;

    // ================= CREATE =================

    @Override
    public AddressResponseDTO create(AddressRequestDTO dto) {

        Address address = AddressMapper.toEntity(dto);

        repository.save(address);

        return AddressMapper.toResponse(address);
    }

    // ================= UPDATE =================

    @Override
    public AddressResponseDTO update(Long id, AddressRequestDTO dto) {

        Address address = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        AddressMapper.patchEntity(address, dto);

        repository.save(address);

        return AddressMapper.toResponse(address);
    }

    // ================= VALIDATION (IMPORTANT) =================

    /**
     * Used during SUBMIT (not draft)
     * Merge DTO + existing DB data → then validate
     */
    public void validateForSubmit(Address existing, AddressRequestDTO dto) {

        String addressLine = dto != null && dto.getAddressLine() != null
                ? dto.getAddressLine()
                : (existing != null ? existing.getAddressLine() : null);

        String city = dto != null && dto.getCity() != null
                ? dto.getCity()
                : (existing != null ? existing.getCity() : null);

        String state = dto != null && dto.getState() != null
                ? dto.getState()
                : (existing != null ? existing.getState() : null);

        String pinCode = dto != null && dto.getPinCode() != null
                ? dto.getPinCode()
                : (existing != null ? existing.getPinCode() : null);

        if (addressLine == null || addressLine.isBlank()) {
            throw new InvalidRequestException("Address line is required");
        }

        if (city == null || city.isBlank()) {
            throw new InvalidRequestException("City is required");
        }

        if (state == null || state.isBlank()) {
            throw new InvalidRequestException("State is required");
        }

        if (pinCode == null || pinCode.isBlank()) {
            throw new InvalidRequestException("Pin code is required");
        }
    }

    // ================= GET =================

    @Override
    public AddressResponseDTO getById(Long id) {

        Address address = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return AddressMapper.toResponse(address);
    }

    // ================= GET ALL =================

    @Override
    public PageResponseDTO<AddressResponseDTO> getAll(Pageable pageable) {

        Page<Address> page = repository.findAll(pageable);

        return PageResponseDTO.<AddressResponseDTO>builder()
                .content(page.map(AddressMapper::toResponse).getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= DELETE =================

    @Override
    public void delete(Long id) {

        Address address = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        repository.delete(address);
    }



}