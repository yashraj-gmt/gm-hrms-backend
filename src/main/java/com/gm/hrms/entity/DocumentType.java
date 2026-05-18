package com.gm.hrms.entity;

import com.gm.hrms.enums.ApplicableType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "document_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class DocumentType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "doc_key", nullable = false, unique = true)
    private String docKey;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "document_type_applicable",
            joinColumns = @JoinColumn(name = "document_type_id")
    )
    @Column(name = "applicable_type")
    private Set<ApplicableType> applicableTypes;

    private Boolean mandatory;

    @Builder.Default
    private Boolean active = true;

    // ── Soft-delete fields ────────────────────────────────────────────────────
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}