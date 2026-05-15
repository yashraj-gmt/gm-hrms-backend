package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LeaveBalanceResponseDTO;
import com.gm.hrms.entity.LeaveBalance;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveBalanceMapper {

    private final UserCodeResolverService codeResolver;

    public LeaveBalanceResponseDTO toResponse(LeaveBalance b) {

        PersonalInformation p = b.getPersonal();

        return LeaveBalanceResponseDTO.builder()
                .employeeName(getFullName(p))
                .employeeCode(codeResolver.getCode(p.getId()))
                .designation(p.getWorkProfile().getDesignation().getName())
                .department(p.getWorkProfile().getDepartment().getName())
                .leaveType(b.getLeaveType().getName())
                .totalLeaves(b.getTotalLeaves())
                .usedLeaves(b.getUsedLeaves())
                .remainingLeaves(b.getRemainingLeaves())
                .year(b.getYear())
                .build();
    }

    private String getFullName(PersonalInformation p) {
        return p.getFirstName() + " " +
                (p.getMiddleName() != null ? p.getMiddleName() + " " : "") +
                p.getLastName();
    }
}