package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternRequestDTO;
import com.gm.hrms.dto.request.InternUpdateDTO;
import com.gm.hrms.dto.response.InternResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface InternService {

    UserCreateResponseDTO create(InternRequestDTO dto, Long personalId);

    public InternResponseDTO update(
            Long id,
            String internJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception;
    InternResponseDTO getById(Long id);

    PageResponseDTO<InternResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}
