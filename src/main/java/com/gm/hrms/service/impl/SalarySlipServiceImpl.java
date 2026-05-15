package com.gm.hrms.service.impl;

import com.gm.hrms.dto.response.SalarySlipResponseDTO;
import com.gm.hrms.entity.SalarySlip;
import com.gm.hrms.enums.SalarySlipStatus;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.SalarySlipMapper;
import com.gm.hrms.report.model.PayslipReportData;
import com.gm.hrms.report.provider.PayslipDataProvider;
import com.gm.hrms.report.service.PayslipReportService;
import com.gm.hrms.repository.SalarySlipRepository;
import com.gm.hrms.service.SalarySlipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalarySlipServiceImpl implements SalarySlipService {

    private final SalarySlipRepository  slipRepo;
    private final PayslipDataProvider   dataProvider;
    private final PayslipReportService  reportService;

    // ── Queries

    @Override
    @Transactional(readOnly = true)
    public SalarySlipResponseDTO getById(Long slipId) {
        return SalarySlipMapper.toResponse(findSlip(slipId));
    }

    @Override
    @Transactional(readOnly = true)
    public SalarySlipResponseDTO getByPersonAndMonth(Long personalId,
                                                     Integer month,
                                                     Integer year) {
        SalarySlip slip = slipRepo
                .findByPersonalInformationIdAndMonthAndYear(personalId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException("Salary slip not found"));
        return SalarySlipMapper.toResponse(slip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalarySlipResponseDTO> getByEmployee(Long personalId) {
        return slipRepo
                .findByPersonalInformationIdOrderByYearDescMonthDesc(personalId)
                .stream()
                .map(SalarySlipMapper::toResponse)
                .toList();
    }

    // ── PDF download ──────────────────────────────────────────────────────

    @Override
    public byte[] downloadPdf(Long slipId) {
        SalarySlip slip = findSlip(slipId);

        if (slip.getStatus() == SalarySlipStatus.DRAFT) {
            throw new IllegalStateException(
                    "Salary slip is still in DRAFT. Admin must finalize the payroll first.");
        }

        // Build the data model — no JasperReports dependency here
        PayslipReportData reportData = dataProvider.buildFrom(slip);

        // Delegate rendering to the report service
        byte[] pdf = reportService.generatePdf(reportData);

        // Mark as downloaded (only after successful generation)
        slip.setStatus(SalarySlipStatus.DOWNLOADED);
        slipRepo.save(slip);

        return pdf;
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private SalarySlip findSlip(Long id) {
        return slipRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary slip not found"));
    }
}