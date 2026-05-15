package com.gm.hrms.enums;

public enum SalaryGenerationStatus {
    DRAFT,       // Generated but not locked
    FINALIZED,   // Locked — slips downloadable
    CANCELLED
}