package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.RoleTransferRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.RoleTransferResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.RoleTransferRequest;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.enums.TransferOtpPurpose;
import com.gm.hrms.enums.TransferStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.repository.RoleTransferRequestRepository;
import com.gm.hrms.service.RoleTransferService;
import com.gm.hrms.service.TransferOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RoleTransferServiceImpl implements RoleTransferService {

    private final RoleTransferRequestRepository repo;
    private final PersonalInformationRepository personRepo;
    private final TransferOtpService            otpService;

    @Override
    @Transactional
    public RoleTransferResponseDTO initiateTransfer(RoleTransferRequestDTO dto) {

        String username = currentUsername();

        // 1. Verify OTP before committing
        otpService.verifyOtp(username, dto.getOtp(), TransferOtpPurpose.ROLE_TRANSFER);

        // 2. Validate recipient
        PersonalInformation recipient = personRepo.findById(dto.getRecipientPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recipient person not found: " + dto.getRecipientPersonId()));

        // 3. Validate date range for non-permanent transfers
        if (!dto.getIsPermanent()) {
            if (dto.getStartDate() == null || dto.getEndDate() == null) {
                throw new InvalidRequestException(
                        "startDate and endDate are required for non-permanent transfers.");
            }
            if (!dto.getEndDate().isAfter(dto.getStartDate())) {
                throw new InvalidRequestException("endDate must be after startDate.");
            }
        }

        // 4. Prevent duplicate active transfer to same recipient
        if (repo.existsByRecipient_IdAndStatus(dto.getRecipientPersonId(), TransferStatus.ACTIVE)) {
            throw new InvalidRequestException(
                    "An active role transfer already exists for this recipient.");
        }

        // 5. Persist
        RoleTransferRequest request = RoleTransferRequest.builder()
                .sourceRole(RoleType.HR)
                .recipient(recipient)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isPermanent(dto.getIsPermanent())
                .reason(dto.getReason())
                .status(TransferStatus.ACTIVE)
                .otpVerified(true)
                .initiatedBy(username)
                .confirmedAt(LocalDateTime.now())
                .build();

        repo.save(request);

        // 6. Consume OTP
        otpService.consumeOtp(username, TransferOtpPurpose.ROLE_TRANSFER);

        return toDTO(request);
    }

    @Override
    public PageResponseDTO<RoleTransferResponseDTO> listTransfers(Pageable pageable) {

        Page<RoleTransferRequest> page = repo.findAllByOrderByCreatedAtDesc(pageable);

        return PageResponseDTO.<RoleTransferResponseDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public RoleTransferResponseDTO cancelTransfer(Long id) {

        RoleTransferRequest request = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role transfer request not found: " + id));

        if (request.getStatus() != TransferStatus.ACTIVE) {
            throw new InvalidRequestException("Only ACTIVE transfers can be cancelled.");
        }

        request.setStatus(TransferStatus.CANCELLED);
        request.setCancelledAt(LocalDateTime.now());
        request.setCancelledBy(currentUsername());

        return toDTO(repo.save(request));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RoleTransferResponseDTO toDTO(RoleTransferRequest r) {
        PersonalInformation p = r.getRecipient();
        return RoleTransferResponseDTO.builder()
                .id(r.getId())
                .sourceRole(r.getSourceRole())
                .recipientName(p.getFirstName() + " " + p.getLastName())
                .recipientDesignation(
                        p.getWorkProfile() != null && p.getWorkProfile().getDesignation() != null
                                ? p.getWorkProfile().getDesignation().getName() : "—")
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .isPermanent(r.getIsPermanent())
                .reason(r.getReason())
                .status(r.getStatus())
                .initiatedBy(r.getInitiatedBy())
                .confirmedAt(r.getConfirmedAt())
                .duration(computeDuration(r))
                .build();
    }

    private String computeDuration(RoleTransferRequest r) {
        if (Boolean.TRUE.equals(r.getIsPermanent())) return "Permanent";
        if (r.getStartDate() == null || r.getEndDate() == null) return "—";
        long days = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
        if (days <= 0) return "—";
        if (days < 7)  return days + " Day" + (days != 1 ? "s" : "");
        long weeks = Math.round(days / 7.0);
        return weeks + " Week" + (weeks != 1 ? "s" : "");
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}