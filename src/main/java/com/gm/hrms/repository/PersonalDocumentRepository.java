package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalDocumentRepository
        extends JpaRepository<PersonalDocument, Long> {

    Optional<PersonalDocument> findByPersonalInformationIdAndDocumentTypeId(
            Long personalId,
            Long documentTypeId
    );
}
