package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.dto.response.InternCourseStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface InternCourseService {

    InternCourseResponseDTO createCourse(InternCourseRequestDTO dto);

    InternCourseResponseDTO updateCourse(Long id, InternCourseRequestDTO dto);

    // Returns ALL courses (active + inactive) — soft-deleted stay visible
    PageResponseDTO<InternCourseResponseDTO> getAllCourses(Pageable pageable);

    InternCourseResponseDTO getCourseById(Long id);

    // Soft delete: sets status = false, record stays in DB and listing
    void deleteCourse(Long id);

    // Counts for stats cards
    InternCourseStatsDTO getStats();
}