package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.CompOffRequestDTO;
import com.gm.hrms.dto.response.CompOffResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.CompOffStatus;
import com.gm.hrms.enums.LeaveTransactionType;
import com.gm.hrms.exception.*;
import com.gm.hrms.mapper.CompOffMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.CompOffRequestService;
import com.gm.hrms.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompOffRequestServiceImpl implements CompOffRequestService {

    private final CompOffRequestRepository repository;
    private final PersonalInformationRepository personalRepository;
    private final LeaveBalanceRepository balanceRepository;
    private final LeaveTransactionService transactionService;

    // ================= APPLY =================
    @Override
    public CompOffResponseDTO apply(CompOffRequestDTO dto) {

        PersonalInformation personal = personalRepository.findById(dto.getPersonalId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CompOffRequest entity = CompOffMapper.toEntity(dto, personal);

        return CompOffMapper.toResponse(repository.save(entity));
    }

    // ================= APPROVE =================
    @Override
    public CompOffResponseDTO approve(Long id, Long approverId) {

        CompOffRequest entity = get(id);

        if (entity.getStatus() != CompOffStatus.PENDING) {
            throw new InvalidRequestException("Already processed");
        }

        entity.setStatus(CompOffStatus.APPROVED);
        entity.setApprovedBy(approverId);
        entity.setApprovedAt(LocalDateTime.now());

        repository.save(entity);

        // 🔥 GET ONLY COMPOFF BALANCE (FIXED)
        LeaveBalance balance = balanceRepository
                .findByPersonalIdAndYear(entity.getPersonal().getId(), entity.getWorkedDate().getYear())
                .stream()
                .filter(b -> Boolean.TRUE.equals(b.getLeaveType().getIsCompOff()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CompOff balance not found"));

        double before = balance.getRemainingLeaves();

        // 🔥 DOUBLE FIX (NO intValue)
        double earned = entity.getEarnedDays();

        balance.setRemainingLeaves(before + earned);

        balanceRepository.save(balance);

        // 🔥 TRANSACTION LOG (DOUBLE FIX)
        transactionService.log(
                balance,
                LeaveTransactionType.ACCRUAL,
                earned,
                before,
                balance.getRemainingLeaves(),
                entity.getId(),
                "Comp-off approved"
        );

        return CompOffMapper.toResponse(entity);
    }

    // ================= REJECT =================
    @Override
    public CompOffResponseDTO reject(Long id, Long approverId) {

        CompOffRequest entity = get(id);

        entity.setStatus(CompOffStatus.REJECTED);
        entity.setApprovedBy(approverId);
        entity.setApprovedAt(LocalDateTime.now());

        return CompOffMapper.toResponse(repository.save(entity));
    }

    // ================= GET =================
    @Override
    public List<CompOffResponseDTO> getByUser(Long personalId) {

        return repository.findByPersonalId(personalId)
                .stream()
                .map(CompOffMapper::toResponse)
                .toList();
    }

    @Override
    public PageResponseDTO<CompOffResponseDTO> getAll(Pageable pageable) {
        Page<CompOffRequest> page = repository.findAll(pageable);
        return PageResponseDTO.<CompOffResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(CompOffMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private CompOffRequest get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
    }
}