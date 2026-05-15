package com.gm.hrms.repository;

import com.gm.hrms.entity.RoleTransferRequest;
import com.gm.hrms.enums.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleTransferRequestRepository extends JpaRepository<RoleTransferRequest, Long> {

    Page<RoleTransferRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByRecipient_IdAndStatus(Long recipientId, TransferStatus status);
}