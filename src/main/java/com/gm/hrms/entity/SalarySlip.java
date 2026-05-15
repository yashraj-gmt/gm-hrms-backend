package com.gm.hrms.entity;

import com.gm.hrms.enums.SalarySlipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "salary_slips",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_salary_slip_person_month_year",
                columnNames = {"personal_information_id", "month", "year"}
        )
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SalarySlip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_generation_id", nullable = false)
    private SalaryGeneration salaryGeneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "employee_name")
    private String employeeName;

    private String department;
    private String designation;
    private String gender;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "pay_date")
    private LocalDate payDate;

    // Bank snapshot
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "pf_number")
    private String pfNumber;

    // ===== PERIOD =====
    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    // ===== ATTENDANCE SUMMARY =====
    @Column(name = "total_working_days")
    private Integer totalWorkingDays;

    @Column(name = "paid_days")
    private Double paidDays;

    @Column(name = "lop_days")
    private Double lopDays;

    // ===== AMOUNTS =====
    @Column(name = "gross_earnings")
    private Double grossEarnings;

    @Column(name = "total_deductions")
    private Double totalDeductions;

    @Column(name = "net_payable")
    private Double netPayable;

    @Column(name = "net_payable_words", length = 500)
    private String netPayableWords;

    @Enumerated(EnumType.STRING)
    private SalarySlipStatus status;

    @OneToMany(
            mappedBy    = "salarySlip",
            cascade     = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalarySlipComponent> components;
}