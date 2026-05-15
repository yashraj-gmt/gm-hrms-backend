package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LeaveTransactionResponseDTO;
import com.gm.hrms.entity.LeaveTransaction;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveTransactionMapper {

    private final UserCodeResolverService codeResolver;

    public LeaveTransactionResponseDTO toResponse(LeaveTransaction t) {

        PersonalInformation p = t.getPersonal();

        return LeaveTransactionResponseDTO.builder()
                .employeeName(getFullName(p))
                .employeeCode(codeResolver.getCode(p.getId())) //  FIX
                .designation(p.getWorkProfile().getDesignation().getName())
                .department(p.getWorkProfile().getDepartment().getName())
                .leaveType(t.getLeaveBalance().getLeaveType().getName())
                .transactionType(t.getType().name())
                .days(t.getDays())
                .beforeBalance(t.getBeforeBalance())
                .afterBalance(t.getAfterBalance())
                .date(t.getTransactionDate())
                .remarks(t.getRemarks())
                .build();
    }

    private String getFullName(PersonalInformation p) {
        return p.getFirstName() + " " +
                (p.getMiddleName() != null ? p.getMiddleName() + " " : "") +
                p.getLastName();
    }
}