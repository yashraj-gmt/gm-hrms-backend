package com.gm.hrms.repository;

import com.gm.hrms.entity.CompOffRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompOffRequestRepository extends JpaRepository<CompOffRequest, Long> {

    List<CompOffRequest> findByPersonalId(Long personalId);
}