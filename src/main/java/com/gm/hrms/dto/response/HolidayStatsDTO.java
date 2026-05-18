package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HolidayStatsDTO {
    private long total;
    private long active;
    private long upcoming;
    private long optional;
}