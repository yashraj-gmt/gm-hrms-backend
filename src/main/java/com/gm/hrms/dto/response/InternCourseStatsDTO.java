package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternCourseStatsDTO {
    private long total;
    private long active;
    private long inactive;
}