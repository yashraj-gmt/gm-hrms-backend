package com.gm.hrms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gm.hrms.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_person_date",
                        columnNames = {"personal_information_id","attendance_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_information_id", nullable = false)
    private PersonalInformation personalInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_profile_id")
    private WorkProfile workProfile;

    @OneToOne(mappedBy = "attendance", fetch = FetchType.LAZY)
    @JsonIgnore
    private AttendanceCalculation calculation;

    @Column(nullable = false, name = "attendance_date")
    private LocalDate attendanceDate;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
}