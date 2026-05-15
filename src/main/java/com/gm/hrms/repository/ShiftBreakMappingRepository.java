package com.gm.hrms.repository;

import com.gm.hrms.entity.ShiftBreakMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftBreakMappingRepository extends JpaRepository<ShiftBreakMapping, Long> {
    void deleteAllByShiftId(Long shiftId);
}