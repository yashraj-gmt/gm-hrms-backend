package com.gm.hrms.audit;

public enum AuditAction {

    // ── Auth ──────────────────────────────────────────────
    LOGIN,
    LOGOUT,
    REFRESH_TOKEN,
    CHANGE_PASSWORD,
    CREATE_AUTH,

    // ── Employee / Person ─────────────────────────────────
    CREATE_EMPLOYEE,
    UPDATE_EMPLOYEE,
    DELETE_EMPLOYEE,
    VIEW_EMPLOYEE,
    CREATE_PERSON,
    VIEW_PERSON,

    // ── Leave Request (workflow) ───────────────────────────
    APPLY_LEAVE,
    APPROVE_LEAVE,
    REJECT_LEAVE,
    CANCEL_LEAVE,
    REQUEST_DOCUMENT,

    // ── Leave Balance / Transaction ────────────────────────
    SEARCH_LEAVE_BALANCE,
    SEARCH_LEAVE_TRANSACTION,

    // ── Leave Policy ──────────────────────────────────────
    CREATE_LEAVE_POLICY,
    UPDATE_LEAVE_POLICY,
    DELETE_LEAVE_POLICY,

    // ── Leave Type ────────────────────────────────────────
    CREATE_LEAVE_TYPE,
    UPDATE_LEAVE_TYPE,
    DELETE_LEAVE_TYPE,

    // ── Leave Application Rule ────────────────────────────
    CREATE_LEAVE_RULE,
    UPDATE_LEAVE_RULE,
    DELETE_LEAVE_RULE,

    // ── Leave Encashment Rule ─────────────────────────────
    CREATE_ENCASHMENT_RULE,
    UPDATE_ENCASHMENT_RULE,
    DELETE_ENCASHMENT_RULE,

    // ── Leave Document ────────────────────────────────────
    UPLOAD_DOCUMENT,
    DELETE_DOCUMENT,

    // ── Policy Mapping ────────────────────────────────────
    CREATE_POLICY_MAPPING,
    UPDATE_POLICY_MAPPING,
    DELETE_POLICY_MAPPING,

    // ── Generic fallback ─────────────────────────────────
    CREATE,
    UPDATE,
    DELETE,
    VIEW,
    EXPORT,


    // ── Attendance ────────────────────────────────────────
    ATTENDANCE_CHECK_IN,
    ATTENDANCE_CHECK_OUT,
    ATTENDANCE_BREAK_START,
    ATTENDANCE_BREAK_END,
    CORRECT_ATTENDANCE,

    // ── Timesheet ─────────────────────────────────────────
    SAVE_TIMESHEET,
    SUBMIT_TIMESHEET,
    APPROVE_TIMESHEET,
    REJECT_TIMESHEET,
    DELETE_TIMESHEET,

    // ── Timesheet Reports ─────────────────────────────────
    EXPORT_REPORT,

    // ── Shift ─────────────────────────────────────────────
    CREATE_SHIFT,
    DELETE_SHIFT,
    UPDATE_SHIFT,
    TOGGLE_SHIFT_STATUS,
    ASSIGN_SHIFT,

    // ── Project ───────────────────────────────────────────
    CREATE_PROJECT,
    UPDATE_PROJECT,
    DELETE_PROJECT,

    // ── Project Assignment ────────────────────────────────
    ASSIGN_PROJECT,
    REMOVE_ASSIGNMENT,

    // ── Address ───────────────────────────────────────────
    CREATE_ADDRESS,
    UPDATE_ADDRESS,
    DELETE_ADDRESS,

    // ── Comp-Off Request ──────────────────────────────────
    APPLY_COMP_OFF,
    APPROVE_COMP_OFF,
    REJECT_COMP_OFF,

    // ── Comp-Off Rule ─────────────────────────────────────
    CREATE_COMP_OFF_RULE,
    UPDATE_COMP_OFF_RULE,
    DELETE_COMP_OFF_RULE,

    // ── Bank & Legal ──────────────────────────────────────
    SAVE_BANK_DETAILS,

    // ── Branch ────────────────────────────────────────────
    CREATE_BRANCH,
    UPDATE_BRANCH,
    DELETE_BRANCH,

    // ── Break Policy ──────────────────────────────────────
    CREATE_BREAK_POLICY,
    UPDATE_BREAK_POLICY,
    DELETE_BREAK_POLICY,

    // ── Carry Forward Rule ────────────────────────────────
    CREATE_CARRY_FORWARD_RULE,
    UPDATE_CARRY_FORWARD_RULE,
    DELETE_CARRY_FORWARD_RULE,

    // ── Holiday ───────────────────────────────────────────
    CREATE_HOLIDAY,
    UPDATE_HOLIDAY,
    DELETE_HOLIDAY,

    // ── Intern ────────────────────────────────────────────
    UPDATE_INTERN,
    DELETE_INTERN,

    // ── Internship Domain ─────────────────────────────────
    CREATE_INTERNSHIP_DOMAIN,
    UPDATE_INTERNSHIP_DOMAIN,
    DELETE_INTERNSHIP_DOMAIN,

    // ── User (unified create) ─────────────────────────────
    CREATE_USER,

    // ── Trainee ───────────────────────────────────────────
    UPDATE_TRAINEE,
    DELETE_TRAINEE,

    // ── Payroll Component ─────────────────────────────────────────────────
    CREATE_PAYROLL_COMPONENT,
    UPDATE_PAYROLL_COMPONENT,
    DELETE_PAYROLL_COMPONENT,

    // ── Salary Structure ──────────────────────────────────────────────────
    CREATE_SALARY_STRUCTURE,
    UPDATE_SALARY_STRUCTURE,

    // ── Salary Generation ─────────────────────────────────────────────────
    GENERATE_SALARY,
    FINALIZE_SALARY,

    // ── Salary Slip ───────────────────────────────────────────────────────
    DOWNLOAD_SALARY_SLIP,
}