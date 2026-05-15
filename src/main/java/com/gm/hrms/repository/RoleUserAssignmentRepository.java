package com.gm.hrms.repository;

import com.gm.hrms.entity.RoleUserAssignment;
import com.gm.hrms.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleUserAssignmentRepository extends JpaRepository<RoleUserAssignment, Long> {

    List<RoleUserAssignment> findAllByRoleTypeAndActiveTrue(RoleType roleType);

    int countByRoleTypeAndActiveTrue(RoleType roleType);

    @Query("""
           SELECT r FROM RoleUserAssignment r
           WHERE r.roleType = :roleType
             AND r.personalInformation.id = :personId
             AND r.active = true
           """)
    java.util.Optional<RoleUserAssignment> findActiveByRoleAndPerson(
            @Param("roleType") RoleType roleType,
            @Param("personId") Long personId);

    void deleteAllByRoleType(RoleType roleType);
}