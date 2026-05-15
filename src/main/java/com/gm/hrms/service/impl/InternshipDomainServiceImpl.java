package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternshipDomainRequestDTO;
import com.gm.hrms.dto.response.InternshipDomainResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.InternshipDomain;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.InternshipDomainMapper;
import com.gm.hrms.repository.InternshipDomainRepository;
import com.gm.hrms.service.InternshipDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipDomainServiceImpl
        implements InternshipDomainService {

    private final InternshipDomainRepository repository;

    // ================= CREATE =================

    @Override
    public InternshipDomainResponseDTO create(
            InternshipDomainRequestDTO dto) {

        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Domain already exists");
        }

        InternshipDomain domain = new InternshipDomain();
        domain.setName(dto.getName().trim());
        domain.setDescription(dto.getDescription());
        domain.setActive(dto.getActive() != null ? dto.getActive() : true);

        repository.save(domain);

        return InternshipDomainMapper.toResponse(domain);
    }

    // ================= UPDATE =================

    @Override
    public InternshipDomainResponseDTO update(
            Long id,
            InternshipDomainRequestDTO dto) {

        InternshipDomain domain = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Domain not found"));

        if (dto.getName() != null &&
                !dto.getName().equalsIgnoreCase(domain.getName())) {

            if (repository.existsByNameIgnoreCase(dto.getName())) {
                throw new DuplicateResourceException("Domain already exists");
            }

            domain.setName(dto.getName().trim());
        }

        if (dto.getDescription() != null)
            domain.setDescription(dto.getDescription());

        if (dto.getActive() != null)
            domain.setActive(dto.getActive());

        return InternshipDomainMapper.toResponse(domain);
    }

    // ================= GET BY ID =================

    @Override
    @Transactional(readOnly = true)
    public InternshipDomainResponseDTO getById(Long id) {

        InternshipDomain domain = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Domain not found"));

        return InternshipDomainMapper.toResponse(domain);
    }

    // ================= GET ALL =================

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<InternshipDomainResponseDTO> getAll(Pageable pageable) {

        Page<InternshipDomain> page = repository.findAll(pageable);

        List<InternshipDomainResponseDTO> content = page.getContent()
                .stream()
                .map(InternshipDomainMapper::toResponse)
                .toList();

        return PageResponseDTO.<InternshipDomainResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // ================= DELETE (SOFT) =================

    @Override
    public void delete(Long id) {

        InternshipDomain domain = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Domain not found"));

        domain.setActive(false);
    }
}