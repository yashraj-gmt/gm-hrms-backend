package com.gm.hrms.dto.response;

import com.gm.hrms.enums.HolidayType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class HolidayResponseDTO {

    private Long id;

    private String holidayName;

    private LocalDate holidayDate;

    private HolidayType holidayType;

    private String description;

    private Boolean isOptional;

    private Boolean isActive;
}