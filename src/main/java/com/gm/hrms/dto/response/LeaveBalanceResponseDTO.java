package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceResponseDTO {

    private Long   id;
    private Long   personalId;

    private String employeeName;
    private String employeeCode;
    private String designation;
    private String department;

    private String leaveType;

    private double totalLeaves;
    private double usedLeaves;
    private double remainingLeaves;

    private Integer year;

    private Long   leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private Boolean isPaid;
    private Boolean isCompOff;

}