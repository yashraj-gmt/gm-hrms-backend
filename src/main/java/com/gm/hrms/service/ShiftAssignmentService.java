package com.gm.hrms.service;

import com.gm.hrms.dto.request.ShiftAssignmentRequestDTO;
import com.gm.hrms.dto.response.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ShiftAssignmentService {
    List<ShiftAssignmentResponseDTO> assign(ShiftAssignmentRequestDTO dto);
    CurrentShiftResponseDTO getMyCurrentShift();
    PageResponseDTO<ShiftAssignmentResponseDTO> getMyShiftHistory(Pageable pageable);
    PageResponseDTO<ShiftAssignmentResponseDTO> getAll(Pageable pageable);
    List<EligiblePersonDTO> searchEligiblePersons(String query);
}