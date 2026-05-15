package com.gm.hrms.specification;

import com.gm.hrms.dto.request.LeaveBalanceFilterDTO;
import com.gm.hrms.entity.LeaveBalance;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class LeaveBalanceSpecification {

    public static Specification<LeaveBalance> filter(LeaveBalanceFilterDTO f) {

        return (root, query, cb) -> {

            Predicate p = cb.conjunction();

            Join<Object, Object> personal = root.join("personal");
            Join<Object, Object> workProfile = personal.join("workProfile", JoinType.LEFT);

            if (f.getPersonalId() != null) {
                p = cb.and(p, cb.equal(personal.get("id"), f.getPersonalId()));
            }

            if (f.getEmployeeName() != null && !f.getEmployeeName().isBlank()) {
                p = cb.and(p, cb.like(
                        cb.lower(cb.concat(
                                personal.get("firstName"),
                                cb.concat(" ", personal.get("lastName"))
                        )),
                        "%" + f.getEmployeeName().toLowerCase() + "%"
                ));
            }

            // ❌ employeeCode filter skip (multi-table issue)

            if (f.getDesignation() != null && !f.getDesignation().isBlank()) {
                p = cb.and(p, cb.equal(
                        workProfile.get("designation").get("name"),
                        f.getDesignation()
                ));
            }

            if (f.getDepartment() != null && !f.getDepartment().isBlank()) {
                p = cb.and(p, cb.equal(
                        workProfile.get("department").get("name"),
                        f.getDepartment()
                ));
            }

            if (f.getYear() != null) {
                p = cb.and(p, cb.equal(root.get("year"), f.getYear()));
            }

            return p;
        };
    }
}