package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.DesignationTransferRequestDTO;
import com.gm.hrms.dto.response.DesignationTransferResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.TransferOtpPurpose;
import com.gm.hrms.enums.TransferStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.DesignationTransferService;
import com.gm.hrms.service.TransferOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DesignationTransferServiceImpl implements DesignationTransferService {

    private final DesignationTransferRequestRepository repo;
    private final PersonalInformationRepository        personRepo;
    private final DesignationRepository                designationRepo;
    private final TransferOtpService                   otpService;

    @Override
    @Transactional
    public DesignationTransferResponseDTO initiateTransfer(DesignationTransferRequestDTO dto) {

        String username = currentUsername();

        // 1. Verify OTP
        otpService.verifyOtp(username, dto.getOtp(), TransferOtpPurpose.DESIGNATION_TRANSFER);

        // 2. Load entities
        PersonalInformation person = personRepo.findById(dto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + dto.getPersonId()));

        Designation fromDesig = designationRepo.findById(dto.getFromDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "From-designation not found: " + dto.getFromDesignationId()));

        // 3. Validate date range
        if (!Boolean.TRUE.equals(dto.getIsPermanent())) {
            if (dto.getStartDate() == null || dto.getEndDate() == null) {
                throw new InvalidRequestException(
                        "startDate and endDate are required for non-permanent transfers.");
            }
            if (!dto.getEndDate().isAfter(dto.getStartDate())) {
                throw new InvalidRequestException("endDate must be after startDate.");
            }
        }

        // 4. Try to resolve toDesignation by name (optional)
        Optional<Designation> toDesig = designationRepo.findByNameIgnoreCase(dto.getToDesignationName());

        // 5. If permanent — actually update WorkProfile designation
        if (Boolean.TRUE.equals(dto.getIsPermanent())) {
            WorkProfile wp = person.getWorkProfile();
            if (wp == null) {
                throw new InvalidRequestException("Person has no WorkProfile to update.");
            }
            Designation resolved = toDesig.orElseGet(() -> {
                // Auto-create new designation
                Designation n = Designation.builder()
                        .name(dto.getToDesignationName())
                        .active(true)
                        .build();
                return designationRepo.save(n);
            });
            wp.setDesignation(resolved);
            // WorkProfile is persisted via cascade / dirty-checking
        }

        // 6. Persist request record
        DesignationTransferRequest request = DesignationTransferRequest.builder()
                .fromDesignation(fromDesig)
                .toDesignationName(dto.getToDesignationName())
                .toDesignation(toDesig.orElse(null))
                .personalInformation(person)
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

        // 7. Consume OTP
        otpService.consumeOtp(username, TransferOtpPurpose.DESIGNATION_TRANSFER);

        return toDTO(request);
    }

    @Override
    public PageResponseDTO<DesignationTransferResponseDTO> listTransfers(Pageable pageable) {
        Page<DesignationTransferRequest> page = repo.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponseDTO.<DesignationTransferResponseDTO>builder()
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
    public DesignationTransferResponseDTO cancelTransfer(Long id) {
        DesignationTransferRequest req = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Designation transfer not found: " + id));

        if (req.getStatus() != TransferStatus.ACTIVE) {
            throw new InvalidRequestException("Only ACTIVE transfers can be cancelled.");
        }

        req.setStatus(TransferStatus.CANCELLED);
        req.setCancelledAt(LocalDateTime.now());
        req.setCancelledBy(currentUsername());

        return toDTO(repo.save(req));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private DesignationTransferResponseDTO toDTO(DesignationTransferRequest r) {
        PersonalInformation p = r.getPersonalInformation();
        return DesignationTransferResponseDTO.builder()
                .id(r.getId())
                .employeeName(p.getFirstName() + " " + p.getLastName())
                .fromDesignation(r.getFromDesignation() != null
                        ? r.getFromDesignation().getName() : "—")
                .toDesignation(r.getToDesignationName())
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

    private String computeDuration(DesignationTransferRequest r) {
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