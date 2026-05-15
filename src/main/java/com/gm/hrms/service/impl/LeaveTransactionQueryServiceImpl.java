package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveTransactionFilterDTO;
import com.gm.hrms.dto.response.LeaveTransactionResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeaveTransaction;
import com.gm.hrms.mapper.LeaveTransactionMapper;
import com.gm.hrms.repository.LeaveTransactionRepository;
import com.gm.hrms.service.LeaveTransactionQueryService;
import com.gm.hrms.specification.LeaveTransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveTransactionQueryServiceImpl implements LeaveTransactionQueryService {

    private final LeaveTransactionRepository repository;
    private final LeaveTransactionMapper mapper; // 🔥 ADD

    @Override
    public PageResponseDTO<LeaveTransactionResponseDTO> getAll(
            LeaveTransactionFilterDTO filter,
            Pageable pageable) {

        if (filter == null) {
            filter = new LeaveTransactionFilterDTO();
        }

        Page<LeaveTransaction> page = repository.findAll(
                LeaveTransactionSpecification.filter(filter),
                pageable
        );

        return PageResponseDTO.<LeaveTransactionResponseDTO>builder()
                .content(page.getContent()
                        .stream()
                        .map(mapper::toResponse) // 🔥 FIX
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}