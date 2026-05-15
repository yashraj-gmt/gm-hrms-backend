package com.gm.hrms.dto.response;

import com.gm.hrms.enums.BreakCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class BreakPolicyResponseDTO {

    private Long          id;
    private String        breakName;
    private BreakCategory breakCategory;
    private LocalTime     breakStart;
    private LocalTime     breakEnd;
    private Integer       breakDurationMinutes;
    private Boolean       isPaid;

    private Boolean       isActive;
}