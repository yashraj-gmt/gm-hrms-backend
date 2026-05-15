package com.gm.hrms.service;

import com.gm.hrms.enums.EmploymentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PersonalDocumentService {

    // ================= CREATE DOCUMENTS =================

    void validateAndSaveDocuments(
            Long personalId,
            EmploymentType employmentType,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    );

    // ================= UPDATE DOCUMENTS =================

    void updateDocuments(
            Long personalId,
            EmploymentType employmentType,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    );
}