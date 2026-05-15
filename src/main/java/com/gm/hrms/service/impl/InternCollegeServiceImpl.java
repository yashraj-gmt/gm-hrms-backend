package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.InternCollegeRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.InternCollegeDetails;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.repository.InternCollegeRepository;
import com.gm.hrms.service.InternCollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternCollegeServiceImpl implements InternCollegeService {

    private final InternCollegeRepository repository;

    @Override
    public void saveOrUpdate(Intern intern,
                             InternCollegeRequestDTO dto) {

        boolean isDraft =
                intern.getPersonalInformation().getRecordStatus() == RecordStatus.DRAFT;

        InternCollegeDetails college =
                repository.findByIntern(intern).orElse(null);

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft) {

            String course = dto != null && dto.getCourseName() != null
                    ? dto.getCourseName()
                    : (college != null ? college.getCourseName() : null);

            String semester = dto != null && dto.getSemester() != null
                    ? dto.getSemester()
                    : (college != null ? college.getSemester() : null);

            String academicYear = dto != null && dto.getAcademicYear() != null
                    ? dto.getAcademicYear()
                    : (college != null ? college.getAcademicYear() : null);

            String enrollment = dto != null && dto.getEnrollmentNumber() != null
                    ? dto.getEnrollmentNumber()
                    : (college != null ? college.getEnrollmentNumber() : null);

            String collegeName = dto != null && dto.getCollegeName() != null
                    ? dto.getCollegeName()
                    : (college != null ? college.getCollegeName() : null);

            String collegeAddress = dto != null && dto.getCollegeAddress() != null
                    ? dto.getCollegeAddress()
                    : (college != null ? college.getCollegeAddress() : null);

            String university = dto != null && dto.getUniversityName() != null
                    ? dto.getUniversityName()
                    : (college != null ? college.getUniversityName() : null);

            String degreeStatus = dto != null && dto.getDegreeCompletionStatus() != null
                    ? dto.getDegreeCompletionStatus()
                    : (college != null ? college.getDegreeCompletionStatus() : null);

            Integer year = dto != null && dto.getYear() != null
                    ? dto.getYear()
                    : (college != null ? college.getYear() : null);

            // ===== REQUIRED CHECK =====

            if (course == null || course.isBlank())
                throw new InvalidRequestException("Course name is required");

            if (semester == null || semester.isBlank())
                throw new InvalidRequestException("Semester is required");

            if (academicYear == null || academicYear.isBlank())
                throw new InvalidRequestException("Academic year is required");

            if (enrollment == null || enrollment.isBlank())
                throw new InvalidRequestException("Enrollment number is required");

            if (collegeName == null || collegeName.isBlank())
                throw new InvalidRequestException("College name is required");

            if (collegeAddress == null || collegeAddress.isBlank())
                throw new InvalidRequestException("College address is required");

            if (university == null || university.isBlank())
                throw new InvalidRequestException("University name is required");

            if (degreeStatus == null || degreeStatus.isBlank())
                throw new InvalidRequestException("Degree completion status is required");

            if (year == null)
                throw new InvalidRequestException("Year is required");
        }

        // ================= CREATE IF NOT EXISTS =================

        if (college == null) {
            college = new InternCollegeDetails();
            college.setIntern(intern);
        }

        // ================= PATCH =================

        if (dto != null) {

            if (dto.getCourseName() != null)
                college.setCourseName(dto.getCourseName());

            if (dto.getSemester() != null)
                college.setSemester(dto.getSemester());

            if (dto.getAcademicYear() != null)
                college.setAcademicYear(dto.getAcademicYear());

            if (dto.getEnrollmentNumber() != null)
                college.setEnrollmentNumber(dto.getEnrollmentNumber());

            if (dto.getCollegeName() != null)
                college.setCollegeName(dto.getCollegeName());

            if (dto.getCollegeAddress() != null)
                college.setCollegeAddress(dto.getCollegeAddress());

            if (dto.getUniversityName() != null)
                college.setUniversityName(dto.getUniversityName());

            if (dto.getDegreeCompletionStatus() != null)
                college.setDegreeCompletionStatus(dto.getDegreeCompletionStatus());

            if (dto.getYear() != null)
                college.setYear(dto.getYear());
        }

        repository.save(college);
    }
}