package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternCourseRequestDTO;
import com.gm.hrms.dto.response.InternCourseResponseDTO;
import com.gm.hrms.dto.response.InternCourseStatsDTO;
import com.gm.hrms.dto.response.PageResponseDTO;
import com.gm.hrms.entity.InternCourse;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.InternCourseMapper;
import com.gm.hrms.repository.InternCourseRepository;
import com.gm.hrms.service.InternCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InternCourseServiceImpl implements InternCourseService {

    private final InternCourseRepository repository;

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public InternCourseResponseDTO createCourse(InternCourseRequestDTO dto) {
        if (repository.existsByNameIgnoreCaseAndDeletedFalse(dto.getName())) {
            throw new DuplicateResourceException("Course already exists with name: " + dto.getName());
        }
        InternCourse course = InternCourseMapper.toEntity(dto);
        repository.save(course);
        return InternCourseMapper.toResponse(course);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public InternCourseResponseDTO updateCourse(Long id, InternCourseRequestDTO dto) {
        InternCourse course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (dto.getName() != null &&
                repository.existsByNameIgnoreCaseAndIdNotAndDeletedFalse(dto.getName().trim(), id)) {
            throw new DuplicateResourceException("Course already exists with name: " + dto.getName());
        }
        InternCourseMapper.patchUpdate(course, dto);
        repository.save(course);
        return InternCourseMapper.toResponse(course);
    }

    // ── GET ALL — non-deleted only ────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<InternCourseResponseDTO> getAllCourses(Pageable pageable) {
        Page<InternCourse> page = repository.findByDeletedFalse(pageable);
        List<InternCourseResponseDTO> content = page.getContent()
                .stream().map(InternCourseMapper::toResponse).toList();
        return buildPageResponse(page, content);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public InternCourseResponseDTO getCourseById(Long id) {

        return InternCourseMapper.toResponse(
                repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Course not found with id: " + id
                        ))
        );
    }

    // ── SOFT DELETE — sets deleted=true, disappears from listing ──────────────
    @Override
    public void deleteCourse(Long id) {
        InternCourse course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        course.setDeleted(true);
        repository.save(course);
    }

    // ── STATS — exclude deleted ───────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public InternCourseStatsDTO getStats() {
        long active   = repository.countByStatusTrueAndDeletedFalse();
        long inactive = repository.countByStatusFalseAndDeletedFalse();
        return InternCourseStatsDTO.builder()
                .total(active + inactive).active(active).inactive(inactive).build();
    }

    // ── Private helper ────────────────────────────────────────────────────────
    private PageResponseDTO<InternCourseResponseDTO> buildPageResponse(
            Page<InternCourse> page,
            List<InternCourseResponseDTO> content) {

        return PageResponseDTO.<InternCourseResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}