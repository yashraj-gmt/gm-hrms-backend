package com.gm.hrms.service;

import com.gm.hrms.dto.request.HolidayRequestDTO;
import com.gm.hrms.dto.response.HolidayResponseDTO;
import com.gm.hrms.dto.response.HolidayStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.enums.HolidayType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HolidayService {

    HolidayResponseDTO create(HolidayRequestDTO dto);

    HolidayResponseDTO update(Long id, HolidayRequestDTO dto);

    HolidayResponseDTO getById(Long id);

    PageResponseDTO<HolidayResponseDTO> getAll(
            String search, HolidayType type, Boolean isActive, Boolean isOptional, Pageable pageable
    );

    /** Global counts for stat cards — independent of search / filter / pagination. */
    HolidayStatsDTO getStats();

    /** Soft-delete: sets isDeleted = true. Record is hidden from listing, retained in DB. */
    void delete(Long id);
}