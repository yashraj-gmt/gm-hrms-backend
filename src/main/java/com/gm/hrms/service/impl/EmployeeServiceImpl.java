package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.EmployeeRequestDTO;
import com.gm.hrms.dto.request.EmployeeStatusUpdateDTO;
import com.gm.hrms.dto.request.EmployeeUpdateDTO;
import com.gm.hrms.dto.response.EmployeeListItemDTO;
import com.gm.hrms.dto.response.EmployeeListResponseDTO;
import com.gm.hrms.dto.response.EmployeeResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.enums.Status;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.EmployeeMapper;
import com.gm.hrms.mapper.InternMapper;
import com.gm.hrms.mapper.TraineeMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.*;
import com.gm.hrms.specification.PersonalInformationSpecification;
import com.gm.hrms.util.PasswordGenerator;
import com.gm.hrms.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository                employeeRepository;
    private final PersonalInformationRepository     personalInformationRepository;
    private final WorkProfileRepository             workProfileRepository;
    private final AuthService                       authService;
    private final EmailService                      emailService;
    private final EmployeeEmploymentService         employeeEmploymentService;
    private final PersonalInformationService        personalInformationService;
    private final PersonalDocumentService           documentService;
    private final FileStorageService                fileStorageService;
    private final ObjectMapper                      objectMapper;
    private final InternRepository internRepository;
    private final TraineeRepository   traineeRepository;

    // =========================================================================
    // CREATE
    // =========================================================================

    @Override
    public UserCreateResponseDTO create(EmployeeRequestDTO dto, Long personalInformationId) {

        PersonalInformation person = personalInformationRepository
                .findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found"));

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        if (!isDraft) {
            if (dto.getRole() == null)
                throw new InvalidRequestException("Role is required");
        }

        String autoCode = generateEmployeeCode();

        Employee employee = EmployeeMapper.toEntity(dto, autoCode);
        employee.setPersonalInformation(person);
        employee = employeeRepository.save(employee);

        // Send credentials only when fully submitted
        if (!isDraft && !authService.existsByPerson(person)) {
            String username    = resolveUsername(person);
            String rawPassword = PasswordGenerator.generatePassword(8);
            authService.createAuthForPerson(person, employee.getRole(), rawPassword);
            emailService.sendCredentials(username, person.getFirstName(), rawPassword);
        }

        if (dto.getEmployment() != null) {
            employeeEmploymentService.saveOrUpdate(employee, dto.getEmployment());
        }

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .id(employee.getId())
                .code(employee.getEmployeeCode())
                .fullName(person.getFirstName() + " " + person.getLastName())
                .role(employee.getRole())
                .active(person.getActive())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    @Override
    public EmployeeResponseDTO update(
            Long id,
            String employeeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        EmployeeUpdateDTO dto = objectMapper.readValue(employeeJson, EmployeeUpdateDTO.class);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        PersonalInformation person = employee.getPersonalInformation();
        boolean isDraft    = person.getRecordStatus() == RecordStatus.DRAFT;
        boolean isSubmitted= person.getRecordStatus() == RecordStatus.SUBMITTED;

        // ── Employee code validation ──────────────────────────────────────────
        if (!isDraft) {
            String code = dto.getEmployeeCode() != null
                    ? dto.getEmployeeCode() : employee.getEmployeeCode();
            if (code == null || code.isBlank())
                throw new InvalidRequestException("Employee code is required");
        }

        // ── Profile image ─────────────────────────────────────────────────────
        if (isSubmitted
                && (profileImage == null || profileImage.isEmpty())
                && (person.getProfileImageUrl() == null || person.getProfileImageUrl().isBlank()))
            throw new InvalidRequestException("Profile image is required");

        if (profileImage != null && !profileImage.isEmpty()) {
            person.setProfileImageUrl(fileStorageService.save(profileImage));
        }

        // ── Personal info patch ───────────────────────────────────────────────
        if (dto.getPersonalInformation() != null) {
            personalInformationService.update(person.getId(), dto.getPersonalInformation());
        }

        // ── Employee code dedup ───────────────────────────────────────────────
        if (dto.getEmployeeCode() != null
                && !dto.getEmployeeCode().equals(employee.getEmployeeCode())) {

            if (!isDraft && dto.getEmployeeCode().isBlank())
                throw new InvalidRequestException("Employee code cannot be blank");

            if (employeeRepository.existsByEmployeeCodeAndIdNot(dto.getEmployeeCode(), id))
                throw new InvalidRequestException(
                        "Employee code already exists: " + dto.getEmployeeCode());

            employee.setEmployeeCode(dto.getEmployeeCode());
        }

        // ── Employment details ────────────────────────────────────────────────
        if (dto.getEmployment() != null) {
            if (!isDraft
                    && dto.getEmployment().getCtc() != null
                    && dto.getEmployment().getCtc() <= 0)
                throw new InvalidRequestException("CTC must be greater than 0");

            employeeEmploymentService.saveOrUpdate(employee, dto.getEmployment());
        }

        // ── Documents ─────────────────────────────────────────────────────────
        documentService.validateAndSaveDocuments(
                person.getId(), person.getEmploymentType(), documents, reasons);

        // ── Auth trigger on first submission ──────────────────────────────────
        if (isSubmitted && !authService.existsByPerson(person)) {
            String username    = resolveUsername(person);
            String rawPassword = PasswordGenerator.generatePassword(8);
            authService.createAuthForPerson(person, employee.getRole(), rawPassword);
            emailService.sendCredentials(username, employee.getEmployeeCode(), rawPassword);
        }

        return EmployeeMapper.toResponse(employee);
    }

    // =========================================================================
    // STATUS UPDATE
    // =========================================================================

    @Override
    public void updateStatus(Long id, EmployeeStatusUpdateDTO dto) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        WorkProfile wp = employee.getPersonalInformation().getWorkProfile();
        if (wp == null)
            throw new InvalidRequestException("Work profile not found for this employee");

        Status status;
        try {
            status = Status.valueOf(dto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(
                    "Invalid status. Accepted: ACTIVE, INACTIVE, ON_HOLD");
        }

        wp.setStatus(status);
    }

    // =========================================================================
    // GET ALL (paginated, filtered, sorted)
    // =========================================================================

   /* @Override
    @Transactional(readOnly = true)
    public EmployeeListResponseDTO getAll(
            int    page,
            int    size,
            String search,
            String status,
            String employmentType,
            String department,
            String dateFrom,
            String dateTo,
            String sortBy,
            String sortDir,
            RecordStatus recordStatus
    ) {
        // Default: show only submitted employees
        RecordStatus rs = (recordStatus != null) ? recordStatus : RecordStatus.SUBMITTED;

        // Parse date strings
        LocalDate from = parseDate(dateFrom);
        LocalDate to   = parseDate(dateTo);

        Pageable pageable = PageRequest.of(page, size);

        Specification<Employee> spec = EmployeeSpecification.withFilters(
                search, status, employmentType, department, from, to, rs, sortBy, sortDir);

        Page<Employee> empPage = employeeRepository.findAll(spec, pageable);

        List<EmployeeListItemDTO> content = empPage.getContent()
                .stream()
                .map(this::toListItem)
                .toList();

        // ── Global summary counts (unfiltered, SUBMITTED only) ─────────────────
        long total      = employeeRepository.countAllSubmitted();
        long activeC    = employeeRepository.countSubmittedByStatus(Status.ACTIVE);
        long inactiveC  = employeeRepository.countSubmittedByStatus(Status.INACTIVE);
        long onHoldC    = employeeRepository.countSubmittedByStatus(Status.ON_HOLD);
        long draftC     = employeeRepository.countDrafts();

        // ── Type counts (SUBMITTED, unfiltered) ───────────────────────────────
        long empC   = employeeRepository.countSubmittedByType(EmploymentType.EMPLOYEE);
        long intC   = employeeRepository.countSubmittedByType(EmploymentType.INTERN);
        long traC   = employeeRepository.countSubmittedByType(EmploymentType.TRAINEE);

        return EmployeeListResponseDTO.builder()
                .content(content)
                .page(empPage.getNumber())
                .size(empPage.getSize())
                .totalElements(empPage.getTotalElements())
                .totalPages(empPage.getTotalPages())
                .first(empPage.isFirst())
                .last(empPage.isLast())
                // Global summary
                .totalEmployees(total)
                .activeCount(activeC)
                .inactiveCount(inactiveC)
                .onHoldCount(onHoldC)
                .draftCount(draftC)
                // Type tabs
                .employeeCount(empC)
                .internCount(intC)
                .traineeCount(traC)
                .build();
    }*/

    @Override
    @Transactional(readOnly = true)
    public EmployeeListResponseDTO getAll(
            int    page,
            int    size,
            String search,
            String status,
            String employmentType,
            String department,
            String dateFrom,
            String dateTo,
            String sortBy,
            String sortDir,
            RecordStatus recordStatus
    ) {
        RecordStatus rs   = (recordStatus != null) ? recordStatus : RecordStatus.SUBMITTED;
        LocalDate    from = parseDate(dateFrom);
        LocalDate    to   = parseDate(dateTo);

        Pageable pageable = PageRequest.of(page, size);

        Specification<PersonalInformation> spec = PersonalInformationSpecification.withFilters(
                search, status, employmentType, department, from, to, rs, sortBy, sortDir);

        Page<PersonalInformation> piPage = personalInformationRepository.findAll(spec, pageable);

        List<EmployeeListItemDTO> content = piPage.getContent()
                .stream()
                .map(this::toListItemFromPI)
                .toList();

        // ── Summary counts (all types, unfiltered) ────────────────────────────────
        long total     = personalInformationRepository.countByRecordStatusAndActiveTrue(RecordStatus.SUBMITTED);
        long activeC   = personalInformationRepository.countByStatusAndWorkProfileStatus(RecordStatus.SUBMITTED, Status.ACTIVE);
        long inactiveC = personalInformationRepository.countByStatusAndWorkProfileStatus(RecordStatus.SUBMITTED, Status.INACTIVE);
        long onHoldC   = personalInformationRepository.countByStatusAndWorkProfileStatus(RecordStatus.SUBMITTED, Status.ON_HOLD);
        long draftC    = personalInformationRepository.countByRecordStatusAndActiveTrue(RecordStatus.DRAFT);

        long empC  = personalInformationRepository.countByStatusAndEmploymentType(RecordStatus.SUBMITTED, EmploymentType.EMPLOYEE);
        long intC  = personalInformationRepository.countByStatusAndEmploymentType(RecordStatus.SUBMITTED, EmploymentType.INTERN);
        long traC  = personalInformationRepository.countByStatusAndEmploymentType(RecordStatus.SUBMITTED, EmploymentType.TRAINEE);

        return EmployeeListResponseDTO.builder()
                .content(content)
                .page(piPage.getNumber())
                .size(piPage.getSize())
                .totalElements(piPage.getTotalElements())
                .totalPages(piPage.getTotalPages())
                .first(piPage.isFirst())
                .last(piPage.isLast())
                .totalEmployees(total)
                .activeCount(activeC)
                .inactiveCount(inactiveC)
                .onHoldCount(onHoldC)
                .draftCount(draftC)
                .employeeCount(empC)
                .internCount(intC)
                .traineeCount(traC)
                .build();
    }

    // =========================================================================
    // GET BY ID
    // =========================================================================

    // ✅ FIX: resolve by personalInformationId, dispatch by employmentType
    @Override
    @Transactional(readOnly = true)
    public Object getById(Long personalInformationId) {

        PersonalInformation pi = personalInformationRepository.findById(personalInformationId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));

        return switch (pi.getEmploymentType()) {

            case EMPLOYEE -> {
                Employee emp = employeeRepository.findByPersonalInformationId(personalInformationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
                yield EmployeeMapper.toResponse(emp);
            }

            case TRAINEE -> {
                Trainee trainee = traineeRepository.findByPersonalInformationId(personalInformationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
                yield TraineeMapper.toResponse(trainee);
            }

            case INTERN -> {
                Intern intern = internRepository.findByPersonalInformationId(personalInformationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Intern not found"));
                yield InternMapper.toResponse(intern);
            }
        };
    }

    // =========================================================================
    // SOFT DELETE
    // =========================================================================

    @Override
    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        PersonalInformation person = employee.getPersonalInformation();

        if (person != null) {
            person.setActive(false);
            if (person.getWorkProfile() != null) {
                person.getWorkProfile().setStatus(Status.INACTIVE);
            }
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /** Map Employee entity → lightweight list DTO */
    private EmployeeListItemDTO toListItem(Employee e) {

        PersonalInformation pi = e.getPersonalInformation();
        WorkProfile         wp = pi != null ? pi.getWorkProfile() : null;
        var                 ct = pi != null ? pi.getContact()      : null;

        String fullName = (pi != null)
                ? (pi.getFirstName() + " " + (pi.getMiddleName() != null ? pi.getMiddleName() + " " : "") + pi.getLastName()).trim()
                : "";

        String email    = (ct != null && ct.getOfficeEmail()  != null) ? ct.getOfficeEmail()   :
                (ct != null ? ct.getPersonalEmail() : null);
        String phone    = (ct != null) ? ((ct.getPersonalPhoneCode() != null && !ct.getPersonalPhoneCode().isEmpty() ? ct.getPersonalPhoneCode() + " " : "") + ct.getPersonalPhone()) : null;

        String deptName = (wp != null && wp.getDepartment()  != null) ? wp.getDepartment().getName()  : null;
        String desigName= (wp != null && wp.getDesignation() != null) ? wp.getDesignation().getName() : null;
        String brName   = (wp != null && wp.getBranch()      != null) ? wp.getBranch().getBranchName()      : null;
        String shiftStr = (wp != null && wp.getShift()       != null) ? wp.getShift().getShiftName()  : null;
        String statusStr= (wp != null && wp.getStatus()      != null) ? wp.getStatus().name()         : null;

        return EmployeeListItemDTO.builder()
                .id(e.getId())
                .employeeCode(e.getEmployeeCode())
                .fullName(fullName)
                .profileImageUrl(pi != null ? pi.getProfileImageUrl() : null)
                .departmentName(deptName)
                .designationName(desigName)
                .branchName(brName)
                .shiftTiming(shiftStr)
                .email(email)
                .phone(phone)
                .joiningDate(wp != null ? wp.getDateOfJoining() : null)
                .status(statusStr)
                .employmentType(pi != null ? pi.getEmploymentType() : null)
                .recordStatus(pi != null ? pi.getRecordStatus() : null)
                .active(pi != null ? pi.getActive() : null)
                .build();
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count() + 1;
        return String.format("GMEP%03d", count);
    }

    private String resolveUsername(PersonalInformation person) {
        if (person.getContact() != null) {
            String office   = person.getContact().getOfficeEmail();
            String personal = person.getContact().getPersonalEmail();
            if (office   != null && !office.isBlank())   return office;
            if (personal != null && !personal.isBlank()) return personal;
        }
        throw new InvalidRequestException("No valid email found for this person");
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private EmployeeListItemDTO toListItemFromPI(PersonalInformation pi) {

        WorkProfile wp = pi.getWorkProfile();
        var         ct = pi.getContact();

        String fullName = (pi.getFirstName() + " "
                + (pi.getMiddleName() != null ? pi.getMiddleName() + " " : "")
                + pi.getLastName()).trim();

        String email = (ct != null && ct.getOfficeEmail()  != null) ? ct.getOfficeEmail()
                : (ct != null ? ct.getPersonalEmail() : null);
        String phone = ct != null ? ((ct.getPersonalPhoneCode() != null && !ct.getPersonalPhoneCode().isEmpty() ? ct.getPersonalPhoneCode() + " " : "") + ct.getPersonalPhone()) : null;

        String deptName  = (wp != null && wp.getDepartment()  != null) ? wp.getDepartment().getName()   : null;
        String desigName = (wp != null && wp.getDesignation() != null) ? wp.getDesignation().getName()  : null;
        String brName    = (wp != null && wp.getBranch()      != null) ? wp.getBranch().getBranchName() : null;
        String statusStr = (wp != null && wp.getStatus()      != null) ? wp.getStatus().name()          : null;

        // ✅ FIX: Always use personalInformationId as the list item id.
        // This is the universal key passed in the URL (/employee/:id, /trainee/:id).
        // getById() will resolve by personalInformationId for all employment types.
        Long   entityId = pi.getId();   // ← always personalInformationId, no branching
        String code     = null;

        EmploymentType type = pi.getEmploymentType();
        if (type == EmploymentType.EMPLOYEE && pi.getEmployee() != null) {
            code = pi.getEmployee().getEmployeeCode();
        } else if (type == EmploymentType.INTERN && pi.getIntern() != null) {
            code = pi.getIntern().getInternCode();
        } else if (type == EmploymentType.TRAINEE && pi.getTrainee() != null) {
            code = pi.getTrainee().getTraineeCode();
        }

        return EmployeeListItemDTO.builder()
                .id(entityId)               // ← personalInformationId
                .employeeCode(code)
                .fullName(fullName)
                .profileImageUrl(pi.getProfileImageUrl())
                .departmentName(deptName)
                .designationName(desigName)
                .branchName(brName)
                .email(email)
                .phone(phone)
                .joiningDate(wp != null ? wp.getDateOfJoining() : null)
                .status(statusStr)
                .employmentType(type)
                .recordStatus(pi.getRecordStatus())
                .active(pi.getActive())
                .build();
    }
}