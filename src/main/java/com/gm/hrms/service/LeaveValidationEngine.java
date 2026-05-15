package com.gm.hrms.service;

import com.gm.hrms.dto.request.LeaveRequestDTO;
import com.gm.hrms.entity.LeaveRequest;

public interface LeaveValidationEngine {

    double calculateTotalDays(LeaveRequestDTO dto, Long policyId);

    void validateBeforeApply(LeaveRequestDTO dto, double totalDays, Long policyId);

    void validateAttendanceConflict(Long personalId, LeaveRequestDTO dto);
}