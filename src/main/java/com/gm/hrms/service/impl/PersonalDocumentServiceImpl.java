package com.gm.hrms.service.impl;

import com.gm.hrms.entity.DocumentType;
import com.gm.hrms.entity.PersonalDocument;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.ApplicableType;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.RecordStatus;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.DocumentTypeRepository;
import com.gm.hrms.repository.PersonalDocumentRepository;
import com.gm.hrms.repository.PersonalInformationRepository;
import com.gm.hrms.service.FileStorageService;
import com.gm.hrms.service.PersonalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalDocumentServiceImpl implements PersonalDocumentService {

    private final DocumentTypeRepository documentTypeRepository;
    private final PersonalDocumentRepository documentRepository;
    private final PersonalInformationRepository personalRepository;
    private final FileStorageService fileStorageService;

    // ======================================================
    // ================= CREATE DOCUMENTS ===================
    // ======================================================

    @Override
    public void validateAndSaveDocuments(
            Long personalId,
            EmploymentType employmentType,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) {

        PersonalInformation personal = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal not found"));

        boolean isDraft = personal.getRecordStatus() == RecordStatus.DRAFT;

        List<DocumentType> requiredDocs =
                documentTypeRepository
                        .findByApplicableTypesContainingAndActiveTrue(
                                ApplicableType.valueOf(employmentType.name())
                        );

        // ================= VALIDATION (ONLY SUBMIT) =================

        if (!isDraft) {

            for (DocumentType type : requiredDocs) {

                String key = type.getDocKey();

                MultipartFile file = documents != null ? documents.get(key) : null;
                String reason = reasons != null ? reasons.get(key) : null;

                Optional<PersonalDocument> existingDoc =
                        documentRepository.findByPersonalInformationIdAndDocumentTypeId(
                                personalId,
                                type.getId()
                        );

                boolean alreadyExists = existingDoc.isPresent() &&
                        existingDoc.get().getFilePath() != null;

                boolean fileMissing = file == null || file.isEmpty();
                boolean reasonMissing = reason == null || reason.isBlank();

                if (Boolean.TRUE.equals(type.getMandatory())) {

                    if (fileMissing && reasonMissing && !alreadyExists) {

                        throw new InvalidRequestException(
                                type.getName()
                                        + " is mandatory. Upload document or provide reason."
                        );
                    }
                }
            }
        }

        // ================= SAVE DOCUMENTS =================

        if (documents == null && (reasons == null || reasons.isEmpty())) {
            return; // nothing to save
        }

        for (DocumentType type : requiredDocs) {

            String key = type.getDocKey();

            MultipartFile file = documents != null ? documents.get(key) : null;
            String reason = reasons != null ? reasons.get(key) : null;

            if ((file != null && !file.isEmpty()) ||
                    (reason != null && !reason.isBlank())) {

                PersonalDocument entity = new PersonalDocument();

                entity.setPersonalInformation(personal);
                entity.setDocumentType(type);

                if (file != null && !file.isEmpty()) {

                    String path = fileStorageService.save(file);
                    entity.setFilePath(path);
                }

                entity.setReason(reason);

                documentRepository.save(entity);
            }
        }
    }

    // ======================================================
    // ================= UPDATE DOCUMENTS ===================
    // ======================================================

    @Override
    public void updateDocuments(
            Long personalId,
            EmploymentType employmentType,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) {

        PersonalInformation personal = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal not found"));

        List<DocumentType> requiredDocs =
                documentTypeRepository
                        .findByApplicableTypesContainingAndActiveTrue(
                                ApplicableType.valueOf(employmentType.name())
                        );

        for (DocumentType type : requiredDocs) {

            String key = type.getDocKey();

            MultipartFile file = documents != null ? documents.get(key) : null;
            String reason = reasons != null ? reasons.get(key) : null;

            // nothing sent
            if (file == null && (reason == null || reason.isBlank()))
                continue;

            Optional<PersonalDocument> existingDoc =
                    documentRepository.findByPersonalInformationIdAndDocumentTypeId(
                            personalId,
                            type.getId()
                    );

            PersonalDocument entity = existingDoc.orElseGet(() -> {

                PersonalDocument newDoc = new PersonalDocument();
                newDoc.setPersonalInformation(personal);
                newDoc.setDocumentType(type);
                return newDoc;
            });

            if (file != null && !file.isEmpty()) {

                String path = fileStorageService.save(file);
                entity.setFilePath(path);
                entity.setReason(null);

            } else {

                entity.setReason(reason);
            }

            documentRepository.save(entity);
        }
    }
}