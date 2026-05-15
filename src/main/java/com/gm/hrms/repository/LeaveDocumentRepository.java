package com.gm.hrms.repository;

import com.gm.hrms.entity.LeaveDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveDocumentRepository extends JpaRepository<LeaveDocument, Long> {

    List<LeaveDocument> findByLeaveRequestIdAndIsActiveTrue(Long leaveRequestId);
}