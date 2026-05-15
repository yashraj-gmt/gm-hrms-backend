package com.gm.hrms.repository;

import com.gm.hrms.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    boolean existsByBranchCode(String branchCode);

    boolean existsByBranchCodeAndIdNot(String branchCode, Long id);

    Optional<Branch> findByBranchCode(String branchCode);

    @Query("SELECT b FROM Branch b WHERE b.parentBranch IS NULL ORDER BY b.sortOrder ASC")
    List<Branch> findRootBranches();
}