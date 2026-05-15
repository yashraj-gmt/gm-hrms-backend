package com.gm.hrms.report.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayslipRow {

    private final String earningName;
    private final Double earningAmount;

    private final String deductionName;
    private final Double deductionAmount;
}