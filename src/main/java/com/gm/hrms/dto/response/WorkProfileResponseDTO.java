package com.gm.hrms.dto.response;

import com.gm.hrms.enums.Status;
import com.gm.hrms.enums.WorkMode;
import com.gm.hrms.enums.WorkingType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WorkProfileResponseDTO {

    private Long id;

    private Long departmentId;

    private Long designationId;

    private Long branchId;

    private Long shiftId;

    private Long reportingManagerProfileId;

    private WorkMode workMode;

    private WorkingType workingType;

    private Status status;
}