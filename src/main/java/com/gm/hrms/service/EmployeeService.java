package com.gm.hrms.service;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeStatusUpdateDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeListResponseDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.enums.RecordStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface EmployeeService {

    UserCreateResponseDTO create(EmployeeRequestDTO dto, Long personalInformationId);

    EmployeeResponseDTO update(
            Long id,
            String employeeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception;

    EmployeeResponseDTO getById(Long id);

    EmployeeListResponseDTO getAll(
            int    page,
            int    size,
            String search,
            String status,
            String employmentType,
            String department,
            String dateFrom,
            String dateTo,
            String sortBy,
            String sortDir,
            RecordStatus recordStatus
    );

    void updateStatus(Long id, EmployeeStatusUpdateDTO dto);

    /** Soft delete – ADMIN only */
    void delete(Long id);
}