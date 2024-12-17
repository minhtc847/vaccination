package com.vaccination.BE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vaccine_injection_schedule")
public class VaccineInjectionSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INJECTION_SCHEDULE_ID", nullable = false, length = 36)
    private long id;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "PLACE")
    private String place;

    @Column(name = "INJECTION_TIMES")
    private int injectionTimes;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACCINE_ID")
    private VaccineVaccine vaccine;


    @Column(name = "INJECT_PER_DAY")
    private int injectPerDay;

//    @Column(name = "STATUS")
//    private String status;

    @OneToMany(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "INJECTION_SCHEDULE_ID")
    private List<VaccineInjectionResult> injectionResults;


    @PreRemove
    private void checkAssociationsBeforeRemove() {
        if (!this.injectionResults.isEmpty()) {
            throw new IllegalStateException("Cannot delete InjectionSchedule with associated VaccineInjectionResults.");
        }
    }
}