package com.vaccination.BE.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vaccine_rule")
public class VaccineRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RULE_ID", nullable = false, length = 36)
    private long id;

    @Column(name = "CONTRAINDICATION", length = 100)
    private String contraindication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACCINE_ID")
    private VaccineVaccine vaccine;



}
