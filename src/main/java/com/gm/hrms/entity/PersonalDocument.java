package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personal_documents")
@Data
public class PersonalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

    private String filePath;

    private String reason; // if document not uploaded
}