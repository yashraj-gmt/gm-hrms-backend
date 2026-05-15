package com.gm.hrms.entity;

import com.gm.hrms.enums.ShiftType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "shifts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shiftName;

    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;

    private Integer graceMinutes;

    private Integer lateMarkAfterMinutes;

    private Integer lateMarkLimit;

    private Integer minimumWorkHours;

    private Boolean overtimeAllowed;

    private Integer overtimeAfterMinutes;

    private Boolean autoCheckout;

    private Boolean isActive;

    // -------- NORMAL SHIFT TIMING --------
    @OneToOne(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShiftTiming timing;

    // -------- CUSTOM DAY SHIFT --------
    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftDayConfig> dayConfigs;

    // -------- BREAK MAPPING --------
    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftBreakMapping> breakMappings;

}