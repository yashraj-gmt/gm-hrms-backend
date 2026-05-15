package com.gm.hrms.dto.response;

import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.enums.WorkMode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InternInternshipResponseDTO {

    private Long domainId;
    private String domainName;

    private LocalDate startDate;
    private LocalDate endDate;
    private String shiftTiming;
    private WorkMode mode;

    private Integer trainingPeriodMonths;
    private String branchName;
    private Double stipend;
    private InternShipType internshipType;
}