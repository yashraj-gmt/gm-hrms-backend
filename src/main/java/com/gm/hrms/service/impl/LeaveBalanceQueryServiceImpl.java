package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.LeaveBalanceFilterDTO;
import com.gm.hrms.dto.response.LeaveBalanceResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.LeaveBalance;
import com.gm.hrms.mapper.LeaveBalanceMapper;
import com.gm.hrms.repository.LeaveBalanceRepository;
import com.gm.hrms.service.LeaveBalanceQueryService;
import com.gm.hrms.specification.LeaveBalanceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveBalanceQueryServiceImpl implements LeaveBalanceQueryService {

    private final LeaveBalanceRepository repository;
    private final LeaveBalanceMapper mapper;

    @Override
    public PageResponseDTO<LeaveBalanceResponseDTO> getAll(
            LeaveBalanceFilterDTO filter,
            Pageable pageable) {

        if (filter == null) {
            filter = new LeaveBalanceFilterDTO();
        }

        Page<LeaveBalance> page = repository.findAll(
                LeaveBalanceSpecification.filter(filter),
                pageable
        );

        return PageResponseDTO.<LeaveBalanceResponseDTO>builder()
                .content(page.getContent()
                        .stream()
                        .map(mapper::toResponse)
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