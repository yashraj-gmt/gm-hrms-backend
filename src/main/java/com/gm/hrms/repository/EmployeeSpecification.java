package com.gm.hrms.repository;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.Status;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    /**
     * Builds a JPA Specification for employee listing with optional filters & sorting.
     *
     * @param search         search term (name / email / code)
     * @param status         work-profile status:  ACTIVE | INACTIVE | ON_HOLD
     * @param employmentType EMPLOYEE | INTERN | TRAINEE
     * @param department     department name (exact)
     * @param dateFrom       joining date range start (inclusive)
     * @param dateTo         joining date range end   (inclusive)
     * @param recordStatus   DRAFT | SUBMITTED  (null → both)
     * @param sortBy         "id" | "name" | "joiningDate"
     * @param sortDir        "asc" | "desc"
     */
    public static Specification<Employee> withFilters(
            String       search,
            String       status,
            String       employmentType,
            String       department,
            LocalDate    dateFrom,
            LocalDate    dateTo,
            RecordStatus recordStatus,
            String       sortBy,
            String       sortDir
    ) {
        return (root, query, cb) -> {

            // ── Joins ──────────────────────────────────────────────────────────
            Join<Object, Object> pi   = root.join("personalInformation");
            Join<Object, Object> wp   = pi.join("workProfile",  JoinType.LEFT);
            Join<Object, Object> ct   = pi.join("contact",      JoinType.LEFT);
            Join<Object, Object> dept = wp.join("department",   JoinType.LEFT);
            Join<Object, Object> desig= wp.join("designation",  JoinType.LEFT);
            Join<Object, Object> shft = wp.join("shift",        JoinType.LEFT);
            Join<Object, Object> br   = wp.join("branch",       JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            // ── Active person only ─────────────────────────────────────────────
            predicates.add(cb.equal(pi.get("active"), true));

            // ── Search: name / email / code ────────────────────────────────────
            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(cb.concat(
                                cb.concat(pi.get("firstName"), cb.literal(" ")),
                                pi.get("lastName")
                        )), like),
                        cb.like(cb.lower(ct.get("officeEmail")),   like),
                        cb.like(cb.lower(ct.get("personalEmail")), like),
                        cb.like(cb.lower(root.get("employeeCode")), like)
                ));
            }

            // ── Status ─────────────────────────────────────────────────────────
            if (status != null && !status.isBlank()) {
                try {
                    Status s = Status.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(wp.get("status"), s));
                } catch (IllegalArgumentException ignored) {}
            }

            // ── Employment type ────────────────────────────────────────────────
            if (employmentType != null && !employmentType.isBlank()) {
                try {
                    EmploymentType et = EmploymentType.valueOf(employmentType.toUpperCase());
                    predicates.add(cb.equal(pi.get("employmentType"), et));
                } catch (IllegalArgumentException ignored) {}
            }

            // ── Department name ────────────────────────────────────────────────
            if (department != null && !department.isBlank()) {
                predicates.add(cb.equal(dept.get("name"), department));
            }

            // ── Joining date range ─────────────────────────────────────────────
            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(wp.get("dateOfJoining"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(wp.get("dateOfJoining"), dateTo));
            }

            // ── Record status (draft / submitted) ──────────────────────────────
            if (recordStatus != null) {
                predicates.add(cb.equal(pi.get("recordStatus"), recordStatus));
            }

            // ── Sorting (skip for COUNT queries) ───────────────────────────────
            boolean isCount = Long.class.equals(query.getResultType())
                    || long.class.equals(query.getResultType());

            if (!isCount) {
                Expression<?> orderExpr;
                if ("name".equalsIgnoreCase(sortBy)) {
                    orderExpr = pi.get("firstName");
                } else if ("joiningDate".equalsIgnoreCase(sortBy)) {
                    orderExpr = wp.get("dateOfJoining");
                } else {
                    orderExpr = root.get("id");
                }

                query.orderBy("desc".equalsIgnoreCase(sortDir)
                        ? cb.desc(orderExpr)
                        : cb.asc(orderExpr));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}