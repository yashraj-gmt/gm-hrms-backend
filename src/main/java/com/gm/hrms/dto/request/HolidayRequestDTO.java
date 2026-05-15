package com.gm.hrms.dto.request;

import com.gm.hrms.enums.HolidayType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HolidayRequestDTO {

    private String holidayName;

    private LocalDate holidayDate;

    private HolidayType holidayType;

    private String description;

    private Boolean isOptional;
}