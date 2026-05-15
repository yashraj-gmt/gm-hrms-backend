package com.gm.hrms.specification;

import com.gm.hrms.dto.request.LeaveTransactionFilterDTO;
import com.gm.hrms.entity.LeaveTransaction;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class LeaveTransactionSpecification {

    public static Specification<LeaveTransaction> filter(LeaveTransactionFilterDTO f) {

        return (root, query, cb) -> {

            Predicate p = cb.conjunction();

            // 🔹 PERSONAL JOIN
            Join<Object, Object> personal = root.join("personal");
            Join<Object, Object> workProfile = personal.join("workProfile", JoinType.LEFT);

            // 🔹 PERSONAL ID
            if (f.getPersonalId() != null) {
                p = cb.and(p, cb.equal(personal.get("id"), f.getPersonalId()));
            }

            // 🔹 NAME (first + last)
            if (f.getEmployeeName() != null && !f.getEmployeeName().isBlank()) {
                p = cb.and(p, cb.like(
                        cb.lower(cb.concat(
                                personal.get("firstName"),
                                cb.concat(" ", personal.get("lastName"))
                        )),
                        "%" + f.getEmployeeName().toLowerCase() + "%"
                ));
            }


            // 🔹 DESIGNATION
            if (f.getDesignation() != null && !f.getDesignation().isBlank()) {
                p = cb.and(p, cb.equal(
                        workProfile.get("designation").get("name"),
                        f.getDesignation()
                ));
            }

            // 🔹 DEPARTMENT
            if (f.getDepartment() != null && !f.getDepartment().isBlank()) {
                p = cb.and(p, cb.equal(
                        workProfile.get("department").get("name"),
                        f.getDepartment()
                ));
            }

            // 🔹 LEAVE TYPE
            if (f.getLeaveTypeId() != null) {
                p = cb.and(p, cb.equal(
                        root.get("leaveBalance").get("leaveType").get("id"),
                        f.getLeaveTypeId()
                ));
            }

            // 🔹 TRANSACTION TYPE
            if (f.getTransactionType() != null) {
                p = cb.and(p, cb.equal(root.get("type"), f.getTransactionType()));
            }

            // 🔹 DATE RANGE
            if (f.getFromDate() != null && f.getToDate() != null) {
                p = cb.and(p, cb.between(
                        root.get("transactionDate"),
                        f.getFromDate().atStartOfDay(),
                        f.getToDate().atTime(23, 59, 59)
                ));
            }

            return p;
        };
    }
}