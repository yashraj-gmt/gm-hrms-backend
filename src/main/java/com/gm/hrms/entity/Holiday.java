package com.gm.hrms.entity;

import com.gm.hrms.enums.HolidayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "holidays")
@Getter
@Setter
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "holiday_name")
    private String holidayName;

    @Column(name = "holiday_date")
    private LocalDate holidayDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type")
    private HolidayType holidayType;

    private String description;

    @Column(name = "is_optional")
    private Boolean isOptional;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}