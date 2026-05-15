package com.gm.hrms.service;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HolidayService {

    HolidayResponseDTO create(HolidayRequestDTO dto);

    HolidayResponseDTO update(Long id, HolidayRequestDTO dto);

    HolidayResponseDTO getById(Long id);

    PageResponseDTO<HolidayResponseDTO> getAll(Pageable pageable);

    void delete(Long id);
}