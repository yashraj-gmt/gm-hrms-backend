package com.gm.hrms.repository;

import com.gm.hrms.entity.DesignationTransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationTransferRequestRepository
        extends JpaRepository<DesignationTransferRequest, Long> {

    Page<DesignationTransferRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
}