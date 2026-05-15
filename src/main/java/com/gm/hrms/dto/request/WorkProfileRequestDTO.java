package com.gm.hrms.dto.request;

import com.gm.hrms.enums.Status;
import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkProfileRequestDTO {

    private Long departmentId;
    private Long designationId;
    private Long branchId;
    private Long shiftId;

    private Long reportingManagerProfileId;

    private WorkMode workMode;
    private WorkingType workingType;
    private Status status;
}