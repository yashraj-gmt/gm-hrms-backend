package com.gm.hrms.entity;

import com.gm.hrms.enums.LeaveTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_id", nullable = false)
    private PersonalInformation personal;

    // 🔥 RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_balance_id", nullable = false)
    private LeaveBalance leaveBalance;

    //  TYPE
    @Enumerated(EnumType.STRING)
    private LeaveTransactionType type;

    //  DATA
    private double days;

    private double beforeBalance;
    private double afterBalance;

    //  OPTIONAL REFERENCE (LeaveRequest / Admin Action)
    private Long referenceId;

    private String remarks;

    private LocalDateTime transactionDate;
}