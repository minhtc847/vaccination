package com.vaccination.BE.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vaccine_injection_result")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VaccineInjectionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INJECTION_RESULT_ID", nullable = false, length = 36)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private VaccineEmployee customer;

    @Column(name = "INJECTION_DATE")
    private LocalDate injectionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACCINE_ID")
    private VaccineVaccine vaccine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INJECTION_SCHEDULE_ID")
    private VaccineInjectionSchedule injectionSchedule;
}