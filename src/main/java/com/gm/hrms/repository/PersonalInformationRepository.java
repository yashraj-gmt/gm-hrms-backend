package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalInformationRepository
        extends JpaRepository<PersonalInformation, Long>,
        JpaSpecificationExecutor<PersonalInformation> {

    @Query("SELECT COUNT(pi) FROM PersonalInformation pi " +
            "WHERE pi.recordStatus = :status AND pi.active = true")
    long countByRecordStatusAndActiveTrue(@Param("status") RecordStatus status);

    @Query("SELECT COUNT(pi) FROM PersonalInformation pi " +
            "JOIN pi.workProfile wp " +
            "WHERE pi.recordStatus = :status AND pi.active = true AND wp.status = :wpStatus")
    long countByStatusAndWorkProfileStatus(
            @Param("status") RecordStatus status,
            @Param("wpStatus") Status wpStatus);

    @Query("SELECT COUNT(pi) FROM PersonalInformation pi " +
            "WHERE pi.recordStatus = :status AND pi.active = true " +
            "AND pi.employmentType = :type")
    long countByStatusAndEmploymentType(
            @Param("status") RecordStatus status,
            @Param("type") EmploymentType type);
}