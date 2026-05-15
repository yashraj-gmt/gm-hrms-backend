package com.gm.hrms.repository;

import com.gm.hrms.entity.ShiftTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftTimingRepository extends JpaRepository<ShiftTiming,Long> {
}