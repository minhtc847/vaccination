package com.vaccination.BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "register_inject")
public class RegisterInject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACCINE_SCHEDULE_ID")
    private VaccineInjectionSchedule vaccineInjectionSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private VaccineEmployee VaccineEmployee;

    @Column(name = "DATE_INJECT")
    LocalDate dateInject;

    @Column(name = "RESULT")
    boolean result;
}
