package com.gm.hrms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
@Getter
@Setter
@SQLRestriction("deleted = false")          // ← auto-filter on EVERY query
public class Branch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "branch_code", nullable = false, unique = true)
    private String branchCode;

    private Boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_branch_id")
    private Branch parentBranch;

    @OneToMany(mappedBy = "parentBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Branch> children = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
}