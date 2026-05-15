package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LeaveTransactionRepository extends JpaRepository<LeaveTransaction, Long> , JpaSpecificationExecutor<LeaveTransaction> {

    List<LeaveTransaction> findByLeaveBalanceId(Long leaveBalanceId);

    List<LeaveTransaction> findByLeaveBalancePersonalId(Long personalId);
}