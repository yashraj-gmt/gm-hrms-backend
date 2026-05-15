package com.gm.hrms.scheduler;

import com.gm.hrms.dto.request.SalaryGenerationRequestDTO;
import com.gm.hrms.service.SalaryGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Automatically generates salary for the previous month
 * on the 1st of every month at 01:00 AM.
 * Admin can still manually trigger via API.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SalaryAutoGenerationScheduler {

    private final SalaryGenerationService service;

    @Scheduled(cron = "0 0 1 1 * ?")   // 01:00 AM on 1st of every month
    public void autoGeneratePreviousMonthSalary() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        log.info("[SCHEDULER] Auto-generating salary for {}/{}",
                lastMonth.getMonthValue(), lastMonth.getYear());
        try {
            SalaryGenerationRequestDTO dto = new SalaryGenerationRequestDTO();
            dto.setMonth(lastMonth.getMonthValue());
            dto.setYear(lastMonth.getYear());

            service.generate(dto);
            log.info("[SCHEDULER] Salary generation completed.");
        } catch (Exception e) {
            log.error("[SCHEDULER] Salary auto-generation failed: {}", e.getMessage());
        }
    }
}