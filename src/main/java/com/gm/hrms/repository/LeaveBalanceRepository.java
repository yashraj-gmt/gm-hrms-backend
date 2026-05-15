package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long>,
        JpaSpecificationExecutor<LeaveBalance> {

    List<LeaveBalance> findByPersonalIdAndYear(Long personalId, Integer year);

    Optional<LeaveBalance> findByPersonalIdAndLeaveTypeIdAndYear(
            Long personalId, Long leaveTypeId, Integer year
    );

    List<LeaveBalance> findByLeaveTypeIdAndYear(Long leaveTypeId, Integer year);

    List<LeaveBalance> findByYear(int year);

    List<LeaveBalance> findByLeaveTypeIdAndYear(Long leaveTypeId, int year);
}