package com.gm.hrms.enums;

public enum LeaveTransactionType {

    // ================= SYSTEM GENERATED =================
    ACCRUAL,          // monthly accrual
    CARRY_FORWARD,    // year-end carry forward

    // ================= USER ACTION =================
    APPLY,            // leave applied
    CANCEL,           // leave cancelled

    // ================= ADMIN =================
    ADJUSTMENT,       // manual add/remove

    // ================= FINANCIAL =================
    ENCASHMENT,       // leave converted to salary

    // ================= COMPOFF =================
    COMP_OFF_CREDIT,  // comp-off approved and added
    COMP_OFF_EXPIRE,  // comp-off expired

    // ================= EXPIRY =================
    EXPIRE            // normal leave expiry
}