package com.gm.hrms.service.impl;

import com.gm.hrms.dto.request.PersonalInformationRequestDTO;
import com.gm.hrms.dto.response.PersonalInformationResponseDTO;
import com.gm.hrms.entity.*;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.DuplicateResourceException;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.AddressMapper;
import com.gm.hrms.mapper.PersonalInformationMapper;
import com.gm.hrms.repository.*;
import com.gm.hrms.service.*;
import com.gm.hrms.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalInformationServiceImpl implements PersonalInformationService {

    private final PersonalInformationRepository personalInformationRepository;
    private final PersonContactRepository contactRepository;
    private final BankDetailsService bankDetailsService;
    private final AddressRepository addressRepository;
    private final WorkProfileService workProfileService;
    private final LeaveBalanceService leaveBalanceService;
    private final WorkProfileRepository workProfileRepository;
    private final LeavePolicyRepository leavePolicyRepository;

    private final AddressService addressService;



    // ================= CREATE =================

    @Override
    public PersonalInformationResponseDTO create(PersonalInformationRequestDTO dto) {

        boolean isDraft = dto.getStatus() == RecordStatus.DRAFT;

        // ================= REQUIRED VALIDATION =================

        if (!isDraft) {

            if (dto.getFirstName() == null || dto.getFirstName().isBlank())
                throw new InvalidRequestException("First name is required");

            if (dto.getMiddleName() == null || dto.getMiddleName().isBlank())
                throw new InvalidRequestException("Middle name is required");

            if (dto.getLastName() == null || dto.getLastName().isBlank())
                throw new InvalidRequestException("Last name is required");

            if (dto.getGender() == null)
                throw new InvalidRequestException("Gender is required");

            if (dto.getDateOfBirth() == null)
                throw new InvalidRequestException("Date of birth is required");

            if (dto.getEmploymentType() == null)
                throw new InvalidRequestException("Employment type is required");

            if (dto.getMaritalStatus() == null)
                throw new InvalidRequestException("Marital status is required");

            if (dto.getSpouseOrParentName() == null || dto.getSpouseOrParentName().isBlank())
                throw new InvalidRequestException("Spouse/Parent name is required");

            if (dto.getPersonalPhone() == null || dto.getPersonalPhone().isBlank())
                throw new InvalidRequestException("Personal phone is required");

            if (dto.getEmergencyPhone() == null || dto.getEmergencyPhone().isBlank())
                throw new InvalidRequestException("Emergency phone is required");

            if (dto.getPersonalEmail() == null || dto.getPersonalEmail().isBlank())
                throw new InvalidRequestException("Personal email is required");

            // Address validation
            addressService.validateForSubmit(
                    null,
                    dto.getAddress() != null ? dto.getAddress().getCurrentAddress() : null
            );
        }

        // ================= EMAIL VALIDATION =================

        if (dto.getPersonalEmail() != null &&
                contactRepository.existsByPersonalEmail(dto.getPersonalEmail())) {
            throw new DuplicateResourceException("Personal email already exists");
        }

        if (dto.getOfficeEmail() != null &&
                contactRepository.existsByOfficeEmail(dto.getOfficeEmail())) {
            throw new DuplicateResourceException("Office email already exists");
        }

        if (dto.getOfficeEmail() != null) {
            ValidationUtils.validateOfficeEmail(dto.getOfficeEmail(), dto.getEmploymentType());
        }

        // ================= SAVE =================

        PersonalInformation entity = PersonalInformationMapper.toEntity(dto);
        entity = personalInformationRepository.save(entity);

        // ================= CONTACT =================

        PersonalInformationContact contact = PersonalInformationContact.builder()
                .personalInformation(entity)
                .personalPhone(dto.getPersonalPhone())
                .emergencyPhone(dto.getEmergencyPhone())
                .personalEmail(dto.getPersonalEmail())
                .officeEmail(dto.getOfficeEmail())
                .build();

        contactRepository.save(contact);
        entity.setContact(contact);

        // ================= ADDRESS =================

        if (dto.getAddress() != null) {

            if (dto.getAddress().getCurrentAddress() != null) {
                Address current = AddressMapper.toEntity(dto.getAddress().getCurrentAddress());
                addressRepository.save(current);
                entity.setCurrentAddress(current);
            }

            if (Boolean.TRUE.equals(dto.getAddress().getSameAsCurrent())) {
                entity.setPermanentAddress(entity.getCurrentAddress());
            } else if (dto.getAddress().getPermanentAddress() != null) {
                Address permanent = AddressMapper.toEntity(dto.getAddress().getPermanentAddress());
                addressRepository.save(permanent);
                entity.setPermanentAddress(permanent);
            }
        }

        // ================= BANK =================

        bankDetailsService.saveOrUpdate(entity, dto.getBankDetails());

        // ================= WORK PROFILE =================

        if (!isDraft && dto.getWorkProfile() == null) {
            throw new InvalidRequestException("Work Profile data required");
        }

        // ── WORK PROFILE ─────────────────────────────────────────────────────────
        if (dto.getWorkProfile() != null) {
            WorkProfile wp;

            if (entity.getWorkProfile() != null) {
                workProfileService.update(entity.getWorkProfile().getId(), dto.getWorkProfile());
                wp = workProfileRepository.findById(entity.getWorkProfile().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Work profile not found"));
            } else {
                workProfileService.create(entity.getId(), dto.getWorkProfile());

                wp = workProfileRepository.findByPersonalInformationId(entity.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Work profile not found after creation"));
            }

            entity.setWorkProfile(wp);
        }



        // ================= LEAVE =================

        if (!isDraft) {
            Long policyId = getPolicyId(entity.getId());
            leaveBalanceService.initializeLeaveBalance(entity.getId(), policyId);
        }

        return PersonalInformationMapper.toResponse(entity);
    }
    @Transactional
    public PersonalInformationResponseDTO update(Long id,
                                                 PersonalInformationRequestDTO dto) {

        System.out.println("..........."+dto.getStatus());

        PersonalInformation entity = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal information not found"));

        RecordStatus oldStatus = entity.getRecordStatus();
        boolean isDraft = dto.getStatus() == RecordStatus.DRAFT;

        PersonalInformationContact contact = entity.getContact();

        // ================= REQUIRED VALIDATION (MERGE) =================

        if (!isDraft) {

            String firstName = getVal(dto.getFirstName(), entity.getFirstName());
            String middleName = getVal(dto.getMiddleName(), entity.getMiddleName());
            String lastName = getVal(dto.getLastName(), entity.getLastName());

            if (firstName == null || firstName.isBlank())
                throw new InvalidRequestException("First name is required");

            if (middleName == null || middleName.isBlank())
                throw new InvalidRequestException("Middle name is required");

            if (lastName == null || lastName.isBlank())
                throw new InvalidRequestException("Last name is required");

            if (getVal(dto.getGender(), entity.getGender()) == null)
                throw new InvalidRequestException("Gender is required");

            if (getVal(dto.getDateOfBirth(), entity.getDateOfBirth()) == null)
                throw new InvalidRequestException("Date of birth is required");

            if (getVal(dto.getEmploymentType(), entity.getEmploymentType()) == null)
                throw new InvalidRequestException("Employment type is required");

            if (getVal(dto.getMaritalStatus(), entity.getMaritalStatus()) == null)
                throw new InvalidRequestException("Marital status is required");


            if (dto.getWorkProfile() == null && entity.getWorkProfile() == null) {

                throw new InvalidRequestException("Work profile is required");
            }

            String spouse = getVal(dto.getSpouseOrParentName(), entity.getSpouseOrParentName());
            if (spouse == null || spouse.isBlank())
                throw new InvalidRequestException("Spouse/Parent name is required");

            String phone = getVal(dto.getPersonalPhone(),
                    contact != null ? contact.getPersonalPhone() : null);

            String email = getVal(dto.getPersonalEmail(),
                    contact != null ? contact.getPersonalEmail() : null);

            if (phone == null || phone.isBlank())
                throw new InvalidRequestException("Personal phone is required");

            if (email == null || email.isBlank())
                throw new InvalidRequestException("Personal email is required");

            addressService.validateForSubmit(
                    entity.getCurrentAddress(),
                    dto.getAddress() != null ? dto.getAddress().getCurrentAddress() : null
            );
        }

        // ================= PATCH =================

        PersonalInformationMapper.updateEntity(entity, dto);

        if (dto.getStatus() != null) {
            entity.setRecordStatus(dto.getStatus());
        }

        // ================= CONTACT =================

        if (contact == null) {
            contact = new PersonalInformationContact();
            contact.setPersonalInformation(entity);
        }

        if (dto.getPersonalEmail() != null &&
                !dto.getPersonalEmail().equals(contact.getPersonalEmail()) &&
                contactRepository.existsByPersonalEmail(dto.getPersonalEmail())) {
            throw new DuplicateResourceException("Personal email already exists");
        }

        if (dto.getOfficeEmail() != null &&
                !dto.getOfficeEmail().equals(contact.getOfficeEmail()) &&
                contactRepository.existsByOfficeEmail(dto.getOfficeEmail())) {
            throw new DuplicateResourceException("Office email already exists");
        }

        PersonalInformationMapper.updateContact(contact, dto);
        contactRepository.save(contact);

        // ================= ADDRESS =================

        if (dto.getAddress() != null) {

            Address current = entity.getCurrentAddress();

            if (dto.getAddress().getCurrentAddress() != null) {

                if (current == null) {
                    current = AddressMapper.toEntity(dto.getAddress().getCurrentAddress());
                    addressRepository.save(current);
                } else {
                    AddressMapper.patchEntity(current, dto.getAddress().getCurrentAddress());
                }

                entity.setCurrentAddress(current);
            }

            if (Boolean.TRUE.equals(dto.getAddress().getSameAsCurrent())) {
                entity.setPermanentAddress(entity.getCurrentAddress());
            } else if (dto.getAddress().getPermanentAddress() != null) {

                Address permanent = entity.getPermanentAddress();

                if (permanent == null) {
                    permanent = AddressMapper.toEntity(dto.getAddress().getPermanentAddress());
                    addressRepository.save(permanent);
                } else {
                    AddressMapper.patchEntity(permanent, dto.getAddress().getPermanentAddress());
                }

                entity.setPermanentAddress(permanent);
            }
        }

        // ================= BANK =================

        bankDetailsService.saveOrUpdate(entity, dto.getBankDetails());

        // ================= WORK PROFILE =================


        if (entity.getWorkProfile() != null) {
            workProfileService.update(entity.getWorkProfile().getId(), dto.getWorkProfile());
        } else if (dto.getWorkProfile() != null) {
            workProfileService.create(entity.getId(), dto.getWorkProfile());
        }

        // ================= LEAVE =================

        if (!isDraft && oldStatus != RecordStatus.SUBMITTED) {
            Long policyId = getPolicyId(entity.getId());
            leaveBalanceService.initializeLeaveBalance(entity.getId(), policyId);
        }

     personalInformationRepository.save(entity);


        return PersonalInformationMapper.toResponse(entity);
    }
    // ================= GET BY ID =================

    @Override
    @Transactional(readOnly = true)
    public PersonalInformationResponseDTO getById(Long id) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation not found"));

        return PersonalInformationMapper.toResponse(personalInformation);
    }

    // ================= GET ALL =================

    @Override
    @Transactional(readOnly = true)
    public List<PersonalInformationResponseDTO> getAll() {

        return personalInformationRepository.findAll()
                .stream()
                .map(PersonalInformationMapper::toResponse)
                .toList();
    }

    // ================= DELETE (SOFT DELETE) =================

    @Override
    public void delete(Long id) {

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation not found"));

        personalInformation.setActive(false);
    }

    // 🔥 FINAL POLICY RESOLUTION (ENTERPRISE)
    private Long getPolicyId(Long personalId) {

        WorkProfile profile = workProfileRepository
                .findByPersonalInformationId(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Work profile not found"));

        EmploymentType type = profile.getPersonalInformation().getEmploymentType();

        LeavePolicy policy = leavePolicyRepository
                .findByEmploymentTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new ResourceNotFoundException("Leave policy not found"));

        return policy.getId();
    }

    private <T> T getVal(T dtoVal, T dbVal) {
        return dtoVal != null ? dtoVal : dbVal;
    }
}