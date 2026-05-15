package com.gm.hrms.service;

import com.gm.hrms.dto.request.TraineeRequestDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.TraineeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface TraineeService {

    UserCreateResponseDTO create(TraineeRequestDTO dto, Long personalInformationId);

    TraineeResponseDTO update(
            Long id,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception;

    TraineeResponseDTO getById(Long id);

    PageResponseDTO<TraineeResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}