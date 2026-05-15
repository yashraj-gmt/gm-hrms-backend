package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.*;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.*;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.TraineeMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.*;
import com.gm.hrms.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository              traineeRepository;
    private final TraineeTrainingRepository      trainingRepository;
    private final TraineeEducationRepository     educationRepository;
    private final TraineeMentorRepository        mentorRepository;
    private final PersonalInformationRepository  personalRepository;
    private final EmployeeRepository             employeeRepository;
    private final WorkProfileRepository          workProfileRepository;

    private final AuthService               authService;
    private final EmailService              emailService;
    private final PersonalInformationService personalInformationService;
    private final PersonalDocumentService   documentService;
    private final FileStorageService        fileStorageService;
    private final ObjectMapper              objectMapper;

    // =========================================================================
    // CREATE
    // =========================================================================
    @Override
    public UserCreateResponseDTO create(TraineeRequestDTO dto, Long personalId) {

        PersonalInformation person = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found"));

        boolean isDraft = person.getRecordStatus() == RecordStatus.DRAFT;

        if (!isDraft && dto == null) {
            throw new InvalidRequestException("Trainee data is required");
        }

        Trainee trainee = new Trainee();
        trainee.setTraineeCode(generateCode());
        trainee.setPersonalInformation(person);
        trainee = traineeRepository.save(trainee);

        if (dto != null) {
            if (dto.getTrainingDetails() != null)
                saveOrUpdateTraining(trainee, dto.getTrainingDetails(), isDraft);

            if (dto.getEducationDetails() != null)
                saveOrUpdateEducation(trainee, dto.getEducationDetails(), isDraft);

            if (dto.getMentorDetails() != null)
                saveOrUpdateMentor(trainee, dto.getMentorDetails(), isDraft);
        }

        // Auth & email — submitted only
        if (!isDraft && !authService.existsByPerson(person)) {
            String rawPassword = PasswordGenerator.generatePassword(8);
            authService.createAuthForPerson(person, RoleType.TRAINEE, rawPassword);
            String email = resolveEmail(person);
            emailService.sendCredentials(email, trainee.getTraineeCode(), rawPassword);
        }

        return UserCreateResponseDTO.builder()
                .personalInformationId(person.getId())
                .id(trainee.getId())
                .code(trainee.getTraineeCode())
                .fullName(person.getFirstName() + " " + person.getLastName())
                .role(RoleType.TRAINEE)
                .active(person.getActive())
                .departmentName(
                        person.getWorkProfile() != null &&
                                person.getWorkProfile().getDepartment() != null
                                ? person.getWorkProfile().getDepartment().getName() : null)
                .createdAt(trainee.getCreatedAt())
                .build();
    }

    // =========================================================================
    // UPDATE
    // =========================================================================
    @Override
    public TraineeResponseDTO update(
            Long id,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        TraineeUpdateDTO dto = objectMapper.readValue(traineeJson, TraineeUpdateDTO.class);

        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        PersonalInformation p = trainee.getPersonalInformation();
        boolean isDraft     = p.getRecordStatus() == RecordStatus.DRAFT;
        boolean isSubmitted = p.getRecordStatus() == RecordStatus.SUBMITTED;

        // ── Trainee code validation ──────────────────────────────────────────
        if (!isDraft) {
            String code = dto.getTraineeCode() != null
                    ? dto.getTraineeCode() : trainee.getTraineeCode();
            if (code == null || code.isBlank())
                throw new InvalidRequestException("Trainee code is required");
        }

        // ── Profile image ────────────────────────────────────────────────────
        if (isSubmitted
                && (profileImage == null || profileImage.isEmpty())
                && (p.getProfileImageUrl() == null || p.getProfileImageUrl().isBlank()))
            throw new InvalidRequestException("Profile image is required");

        if (profileImage != null && !profileImage.isEmpty()) {
            p.setProfileImageUrl(fileStorageService.save(profileImage));
        }

        // ── Personal info ────────────────────────────────────────────────────
        if (dto.getPersonalInformation() != null) {
            personalInformationService.update(p.getId(), dto.getPersonalInformation());
        }

        // ── Trainee code dedup ───────────────────────────────────────────────
        if (dto.getTraineeCode() != null
                && !dto.getTraineeCode().equals(trainee.getTraineeCode())) {

            if (!isDraft && dto.getTraineeCode().isBlank())
                throw new InvalidRequestException("Trainee code cannot be blank");

            if (traineeRepository.existsByTraineeCodeAndIdNot(dto.getTraineeCode(), id))
                throw new InvalidRequestException(
                        "Trainee code already exists: " + dto.getTraineeCode());

            trainee.setTraineeCode(dto.getTraineeCode());
        }

        // ── Sub-details ──────────────────────────────────────────────────────
        if (dto.getTrainingDetails() != null)
            saveOrUpdateTraining(trainee, dto.getTrainingDetails(), isDraft);

        if (dto.getEducationDetails() != null)
            saveOrUpdateEducation(trainee, dto.getEducationDetails(), isDraft);

        if (dto.getMentorDetails() != null)
            saveOrUpdateMentor(trainee, dto.getMentorDetails(), isDraft);

        // ── Documents ────────────────────────────────────────────────────────
        if (!isDraft) {
            documentService.validateAndSaveDocuments(
                    p.getId(), p.getEmploymentType(), documents, reasons);
        }

        // ── Auth on first submission ─────────────────────────────────────────
        if (isSubmitted && !authService.existsByPerson(p)) {
            String rawPassword = PasswordGenerator.generatePassword(8);
            authService.createAuthForPerson(p, RoleType.TRAINEE, rawPassword);
            String email = resolveEmail(p);
            emailService.sendCredentials(email, trainee.getTraineeCode(), rawPassword);
        }

        return TraineeMapper.toResponse(trainee);
    }

    // =========================================================================
    // GET BY ID
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public TraineeResponseDTO getById(Long id) {
        return traineeRepository.findById(id)
                .map(TraineeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));
    }

    // =========================================================================
    // GET ALL
    // =========================================================================
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<TraineeResponseDTO> getAll(Pageable pageable) {
        Page<Trainee> page = traineeRepository.findAll(pageable);
        List<TraineeResponseDTO> content = page.getContent()
                .stream().map(TraineeMapper::toResponse).toList();
        return PageResponseDTO.<TraineeResponseDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    // =========================================================================
    // DELETE (soft)
    // =========================================================================
    @Override
    public void delete(Long id) {
        Trainee trainee = traineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found"));

        PersonalInformation person = trainee.getPersonalInformation();
        if (person != null) {
            person.setActive(false);
            if (person.getWorkProfile() != null)
                person.getWorkProfile().setStatus(Status.INACTIVE);
        }
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private void saveOrUpdateTraining(Trainee trainee,
                                      TraineeTrainingRequestDTO dto,
                                      boolean isDraft) {
        if (dto == null) return;

        TraineeTrainingDetails td =
                trainingRepository.findByTrainee(trainee).orElse(null);

        // Validation on submit
        if (!isDraft) {
            LocalDate start = dto.getStartDate() != null ? dto.getStartDate()
                    : (td != null ? td.getStartDate() : null);
            LocalDate end   = dto.getEndDate() != null ? dto.getEndDate()
                    : (td != null ? td.getEndDate() : null);
            Integer period  = dto.getTrainingPeriodMonths() != null ? dto.getTrainingPeriodMonths()
                    : (td != null ? td.getTrainingPeriodMonths() : null);

            if (start == null)  throw new InvalidRequestException("Training start date is required");
            if (end == null)    throw new InvalidRequestException("Training end date is required");
            if (period == null) throw new InvalidRequestException("Training period is required");
            if (end.isBefore(start))
                throw new InvalidRequestException("End date cannot be before start date");
        }

        if (td == null) { td = new TraineeTrainingDetails(); td.setTrainee(trainee); }

        if (dto.getStartDate() != null) {
            td.setStartDate(dto.getStartDate());
            // Sync date of joining in WorkProfile
            workProfileRepository.findByPersonalInformationId(
                            trainee.getPersonalInformation().getId())
                    .ifPresent(wp -> wp.setDateOfJoining(dto.getStartDate()));
        }
        if (dto.getEndDate()             != null) td.setEndDate(dto.getEndDate());
        if (dto.getTrainingPeriodMonths()!= null) td.setTrainingPeriodMonths(dto.getTrainingPeriodMonths());
        if (dto.getStipend()             != null) td.setStipend(dto.getStipend());

        // WorkProfile: workMode & workingType
        if (dto.getWorkMode() != null || dto.getWorkingType() != null) {
            workProfileRepository.findByPersonalInformationId(
                            trainee.getPersonalInformation().getId())
                    .ifPresent(wp -> {
                        if (dto.getWorkMode() != null) {
                            try { wp.setWorkMode(WorkMode.valueOf(dto.getWorkMode())); }
                            catch (IllegalArgumentException ignored) {}
                        }
                        if (dto.getWorkingType() != null) {
                            try { wp.setWorkingType(WorkingType.valueOf(dto.getWorkingType())); }
                            catch (IllegalArgumentException ignored) {}
                        }
                    });
        }

        trainingRepository.save(td);
        trainee.setTrainingDetails(td);
    }

    private void saveOrUpdateEducation(Trainee trainee,
                                       TraineeEducationRequestDTO dto,
                                       boolean isDraft) {
        if (dto == null) return;

        TraineeEducationDetails ed =
                educationRepository.findByTrainee(trainee).orElse(null);

        // Validation on submit
        if (!isDraft) {
            String hsc  = dto.getHscCompletion()      != null ? dto.getHscCompletion()
                    : (ed != null ? ed.getHscCompletion() : null);
            Integer hscY= dto.getHscYear()             != null ? dto.getHscYear()
                    : (ed != null ? ed.getHscYear() : null);
            String bach  = dto.getBachelorCompletion() != null ? dto.getBachelorCompletion()
                    : (ed != null ? ed.getBachelorCompletion() : null);
            Integer bachY= dto.getBachelorYear()       != null ? dto.getBachelorYear()
                    : (ed != null ? ed.getBachelorYear() : null);
            String degName = dto.getDegreeName()       != null ? dto.getDegreeName()
                    : (ed != null ? ed.getDegreeName() : null);
            String uni     = dto.getUniversityName()   != null ? dto.getUniversityName()
                    : (ed != null ? ed.getUniversityName() : null);

            if (hsc  == null || hsc.isBlank())   throw new InvalidRequestException("HSC completion is required");
            if (hscY  == null)                    throw new InvalidRequestException("HSC year is required");
            if (bach == null || bach.isBlank())   throw new InvalidRequestException("Bachelor completion is required");
            if (bachY == null)                    throw new InvalidRequestException("Bachelor year is required");
            if (degName == null || degName.isBlank()) throw new InvalidRequestException("Degree name is required");
            if (uni == null || uni.isBlank())     throw new InvalidRequestException("University name is required");
        }

        if (ed == null) { ed = new TraineeEducationDetails(); ed.setTrainee(trainee); }

        if (dto.getHscCompletion()           != null) ed.setHscCompletion(dto.getHscCompletion());
        if (dto.getHscYear()                 != null) ed.setHscYear(dto.getHscYear());
        if (dto.getBachelorCompletion()      != null) ed.setBachelorCompletion(dto.getBachelorCompletion());
        if (dto.getBachelorYear()            != null) ed.setBachelorYear(dto.getBachelorYear());
        if (dto.getMasterCompletion()        != null) ed.setMasterCompletion(dto.getMasterCompletion());
        if (dto.getMasterYear()              != null) ed.setMasterYear(dto.getMasterYear());
        if (dto.getDegreeName()              != null) ed.setDegreeName(dto.getDegreeName());
        if (dto.getDegreeResult()            != null) ed.setDegreeResult(dto.getDegreeResult());
        if (dto.getUniversityName()          != null) ed.setUniversityName(dto.getUniversityName());
        if (dto.getUniversityAddress()       != null) ed.setUniversityAddress(dto.getUniversityAddress());
        if (dto.getTrainingCompletionStatus()!= null) ed.setTrainingCompletionStatus(dto.getTrainingCompletionStatus());

        educationRepository.save(ed);
        trainee.setEducationDetails(ed);
    }

    private void saveOrUpdateMentor(Trainee trainee,
                                    TraineeMentorRequestDTO dto,
                                    boolean isDraft) {
        if (dto == null) return;

        TraineeMentorDetails md =
                mentorRepository.findByTrainee(trainee).orElse(null);

        if (!isDraft) {
            Long mentorId = dto.getMentorEmployeeId() != null ? dto.getMentorEmployeeId()
                    : (md != null && md.getMentor() != null ? md.getMentor().getId() : null);
            if (mentorId == null)
                throw new InvalidRequestException("Mentor is required");
        }

        if (md == null) { md = new TraineeMentorDetails(); md.setTrainee(trainee); }

        if (dto.getMentorEmployeeId() != null) {
            Employee mentor = employeeRepository.findById(dto.getMentorEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
            md.setMentor(mentor);
        }
        if (dto.getSupervisorEmployeeId() != null) {
            Employee sup = employeeRepository.findById(dto.getSupervisorEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found"));
            md.setSupervisor(sup);
        }

        mentorRepository.save(md);
        trainee.setMentorDetails(md);
    }

    private String generateCode() {
        long count = traineeRepository.count() + 1;
        return String.format("GMTR%03d", count);
    }

    private String resolveEmail(PersonalInformation person) {
        if (person.getContact() != null) {
            String office   = person.getContact().getOfficeEmail();
            String personal = person.getContact().getPersonalEmail();
            if (office   != null && !office.isBlank())   return office;
            if (personal != null && !personal.isBlank()) return personal;
        }
        throw new InvalidRequestException("No valid email found for this person");
    }

    // Import missing for LocalDate in helper — added via the class-level field reference
    // The entity import is already pulled via entity.*
}