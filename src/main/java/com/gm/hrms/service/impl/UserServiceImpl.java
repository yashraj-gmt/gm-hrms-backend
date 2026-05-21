package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.*;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.dto.response.UserProfileResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.UserAuth;
import com.gm.hrms.entity.WorkProfile;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.PersonalInformationContactRepository;
import com.gm.hrms.repository.UserAuthRepository;
import com.gm.hrms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PersonalDocumentService documentService;
    private final PersonalInformationService personalService;
    private final EmployeeService employeeService;
    private final InternService internService;
    private final TraineeService traineeService;
    private final ObjectMapper mapper;
    private final FileStorageService fileStorageService;
    private final UserAuthRepository userAuthRepository;
    private final EmployeeRepository employeeRepository;
    private final PersonalInformationContactRepository contactRepository;

    // =========================================================================
    // CREATE
    // =========================================================================

    @Override
    public UserCreateResponseDTO create(
            String personalInformationJson,
            String internJson,
            String employeeJson,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception {

        // ================= PARSE =================

        PersonalInformationRequestDTO personalInformation =
                mapper.readValue(personalInformationJson, PersonalInformationRequestDTO.class);

        InternRequestDTO intern = internJson != null
                ? mapper.readValue(internJson, InternRequestDTO.class) : null;

        EmployeeRequestDTO employee = employeeJson != null
                ? mapper.readValue(employeeJson, EmployeeRequestDTO.class) : null;

        TraineeRequestDTO trainee = traineeJson != null
                ? mapper.readValue(traineeJson, TraineeRequestDTO.class) : null;

        // ================= STATUS =================

        boolean isDraft = personalInformation.getStatus() == RecordStatus.DRAFT;

        // ================= PROFILE IMAGE =================
        // FIX: Only enforce "profile image required" for SUBMITTED records.
        // DRAFT records may be saved without a photo (profileImageUrl will be null,
        // which is now allowed after removing nullable=false from the entity column).

        if (!isDraft && (profileImage == null || profileImage.isEmpty())) {
            throw new InvalidRequestException("Profile image is required");
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImagePath = fileStorageService.save(profileImage);
            personalInformation.setProfileImageUrl(profileImagePath);
        }
        // For drafts with no photo, profileImageUrl stays null — that is now fine.

        // ================= PERSONAL =================

        PersonalInformationResponseDTO person =
                personalService.create(personalInformation);

        EmploymentType type = personalInformation.getEmploymentType();

        // ================= DOCUMENT =================

        documentService.validateAndSaveDocuments(
                person.getId(),
                type,
                documents,
                reasons
        );

        // ================= ROUTING =================

        return switch (type) {

            case EMPLOYEE -> {
                if (!isDraft && employee == null)
                    throw new InvalidRequestException("Employee data required");

                yield employee != null
                        ? employeeService.create(employee, person.getId())
                        : null;
            }

            case INTERN -> {
                if (!isDraft && intern == null)
                    throw new InvalidRequestException("Intern data required");

                yield intern != null
                        ? internService.create(intern, person.getId())
                        : null;
            }

            case TRAINEE -> {
                if (!isDraft && trainee == null)
                    throw new InvalidRequestException("Trainee data required");

                yield trainee != null
                        ? traineeService.create(trainee, person.getId())
                        : null;
            }
        };
    }

    // =========================================================================
    // ME
    // =========================================================================

    @Override
    public UserProfileResponseDTO getMe(String username) {
        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildProfileResponse(auth);
    }

    @Override
    public UserProfileResponseDTO updateMe(String username, ProfileUpdateRequestDTO dto) {
        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        PersonalInformation person = auth.getPersonalInformation();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            String[] parts = dto.getName().trim().split(" ", 2);
            person.setFirstName(parts[0]);
            if (parts.length > 1) person.setLastName(parts[1]);
        }
        if (dto.getPhone() != null && person.getContact() != null) {
            person.getContact().setPersonalPhone(dto.getPhone());
        }
        return buildProfileResponse(auth);
    }

    @Override
    public UserProfileResponseDTO updateAvatar(String username, MultipartFile image) throws Exception {
        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String path = fileStorageService.save(image);
        auth.getPersonalInformation().setProfileImageUrl(path);
        return buildProfileResponse(auth);
    }

    // =========================================================================
    // UNIQUENESS CHECKS
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, String type) {
        if (email == null || email.isBlank()) return true;
        String normalized = email.trim().toLowerCase();

        if ("OFFICE".equalsIgnoreCase(type)) {
            if (contactRepository.existsByOfficeEmailIgnoreCase(normalized)) return false;
        } else if ("PERSONAL".equalsIgnoreCase(type)) {
            if (contactRepository.existsByPersonalEmailIgnoreCase(normalized)) return false;
        } else {
            // fallback: check both
            if (contactRepository.existsByOfficeEmailIgnoreCase(normalized))   return false;
            if (contactRepository.existsByPersonalEmailIgnoreCase(normalized)) return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmployeeCodeAvailable(String code) {
        if (code == null || code.isBlank()) return true;
        return !employeeRepository.existsByEmployeeCode(code.trim());
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private UserProfileResponseDTO buildProfileResponse(UserAuth auth) {
        PersonalInformation p = auth.getPersonalInformation();
        WorkProfile w = p.getWorkProfile();
        String email = p.getContact() != null
                ? (p.getContact().getOfficeEmail() != null
                ? p.getContact().getOfficeEmail()
                : p.getContact().getPersonalEmail())
                : null;
        return UserProfileResponseDTO.builder()
                .id(p.getId())
                .fullName(p.getFirstName() + (p.getLastName() != null ? " " + p.getLastName() : ""))
                .email(email)
                .phone(p.getContact() != null ? p.getContact().getPersonalPhone() : null)
                .designation(w != null ? (w.getDesignation() != null ? w.getDesignation().getName() : null) : null)
                .department(w != null ? (w.getDepartment() != null ? w.getDepartment().getName() : null) : null)
                .branch(w != null ? (w.getBranch() != null ? w.getBranch().getBranchName() : null) : null)
                .profileImageUrl(p.getProfileImageUrl())
                .role(auth.getRole().name())
                .build();
    }
}