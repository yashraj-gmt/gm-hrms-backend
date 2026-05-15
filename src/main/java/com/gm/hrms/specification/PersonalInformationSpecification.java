package com.gm.hrms.specification;

import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.WorkProfile;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.Status;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonalInformationSpecification {

    public static Specification<PersonalInformation> withFilters(
            String search,
            String status,
            String employmentType,
            String department,
            LocalDate from,
            LocalDate to,
            RecordStatus recordStatus,
            String sortBy,
            String sortDir
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter active records only
            predicates.add(cb.equal(root.get("active"), true));

            // RecordStatus (SUBMITTED / DRAFT)
            if (recordStatus != null) {
                predicates.add(cb.equal(root.get("recordStatus"), recordStatus));
            }

            // EmploymentType filter
            if (employmentType != null && !employmentType.isBlank()) {
                try {
                    predicates.add(cb.equal(
                            root.get("employmentType"),
                            EmploymentType.valueOf(employmentType)
                    ));
                } catch (IllegalArgumentException ignored) {}
            }

            // WorkProfile join (LEFT so drafts without a profile still appear)
            Join<PersonalInformation, WorkProfile> wp =
                    root.join("workProfile", JoinType.LEFT);

            // Status filter (WorkProfile.status)
            if (status != null && !status.isBlank()) {
                try {
                    predicates.add(cb.equal(wp.get("status"), Status.valueOf(status)));
                } catch (IllegalArgumentException ignored) {}
            }

            // Department name filter
            if (department != null && !department.isBlank()) {
                Join<?, ?> dept = wp.join("department", JoinType.LEFT);
                predicates.add(cb.like(
                        cb.lower(dept.get("name")),
                        "%" + department.toLowerCase() + "%"
                ));
            }

            // Joining date range (WorkProfile.dateOfJoining)
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(wp.get("dateOfJoining"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(wp.get("dateOfJoining"), to));
            }

            // Free-text search (name / email / phone)
            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                Join<?, ?> ct = root.join("contact", JoinType.LEFT);

                Expression<String> fullName = cb.lower(cb.concat(
                        cb.concat(root.get("firstName"), " "),
                        root.get("lastName")
                ));
                predicates.add(cb.or(
                        cb.like(fullName, like),
                        cb.like(cb.lower(ct.get("officeEmail")),   like),
                        cb.like(cb.lower(ct.get("personalEmail")), like),
                        cb.like(ct.get("personalPhone"),           like)
                ));
            }

            // Sorting
            boolean asc = !"desc".equalsIgnoreCase(sortDir);
            Expression<?> sortExpr = switch (sortBy != null ? sortBy : "id") {
                case "name"        -> root.get("firstName");
                case "joiningDate" -> wp.get("dateOfJoining");
                default            -> root.get("id");
            };
            query.orderBy(asc ? cb.asc(sortExpr) : cb.desc(sortExpr));

            // De-duplicate rows that can appear due to LEFT JOINs
            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}