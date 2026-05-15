package com.gm.hrms.repository;

import com.gm.hrms.entity.RolePermission;
import com.gm.hrms.enums.ModuleType;
import com.gm.hrms.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findAllByRoleType(RoleType roleType);

    Optional<RolePermission> findByRoleTypeAndModule(RoleType roleType, ModuleType module);

    void deleteAllByRoleType(RoleType roleType);
}