package com.vaccination.BE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vaccine_vaccine")
public class VaccineVaccine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACCINE_ID", nullable = false, length = 36)
    private long id;

    @Column(name = "INDICATION", length = 200)
    private String indication;

    @Column(name = "NUMBER_OF_INJECTION")
    private Integer numberOfInjection;

    @Column(name = "TIME_BEGIN_NEXT_INJECTION")
    private Integer timeBeginNextInjection;

    @Column(name = "`USAGE`", length = 200)
    private String usage;

    @Column(name = "`ORIGIN`", length = 200)
    private String origin;

    @Column(name = "VACCINE_NAME", length = 100, unique = true, columnDefinition = "VARCHAR(100) COLLATE utf8mb4_unicode_ci")
    private String vaccineName;

    @Column(name = "VACCINE_STATUS", length = 50)
    private boolean status;

    @Column(name = "VACCINE_TOTAL_INJECT")
    private Integer totalInject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACCINE_TYPE_ID")
    private VaccineVaccineType vaccineType;

    @OneToMany(mappedBy = "vaccine")
    private Set<VaccineInjectionResult> vaccineInjectionResults = new LinkedHashSet<>();

    @OneToMany(mappedBy = "vaccine")
    @Builder.Default
    private Set<VaccineInjectionSchedule> vaccineInjectionSchedules = new LinkedHashSet<>();

    public Long getVaccineTypeId() {
        return (vaccineType != null) ? vaccineType.getId() : null;
    }

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VaccineRule> contraindications = new LinkedHashSet<>();

    @PreRemove
    private void checkAssociationsBeforeRemove() {
        if (!this.vaccineInjectionSchedules.isEmpty()) {
            throw new IllegalStateException("Cannot delete Vaccine with associated VaccineInjectionSchedule.");
        }
    }
}