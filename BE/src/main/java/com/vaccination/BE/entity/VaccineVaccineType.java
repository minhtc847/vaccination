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
@Table(name = "vaccine_vaccine_type")
public class VaccineVaccineType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACCINE_TYPE_ID", nullable = false, length = 36)
    private long id;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "VACCINE_TYPE_NAME", length = 50,  unique = true, columnDefinition = "VARCHAR(100) COLLATE utf8mb4_unicode_ci")
    private String vaccineTypeName;

    @Column(name = "VACCINE_TYPE_IMAGE")
    private String image;

    @Column(name = "VACCINE_TYPE_STATUS", length = 50)
    private boolean status;

    @Column(name ="VACCINE_TYPE_CODE", length = 50)
    private String code;

    @OneToMany(mappedBy = "vaccineType")
    private Set<VaccineVaccine> vaccineVaccines = new LinkedHashSet<>();

}