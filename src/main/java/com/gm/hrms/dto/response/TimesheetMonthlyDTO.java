package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimesheetMonthlyDTO {

    private Long personId;

    private String personName;

    private Integer totalEntries;

    private String totalTime; // HH:mm format

}