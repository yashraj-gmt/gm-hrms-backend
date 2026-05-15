package com.gm.hrms.repository;

import com.gm.hrms.entity.ShiftDayConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftDayConfigRepository extends JpaRepository<ShiftDayConfig,Long> {
}
