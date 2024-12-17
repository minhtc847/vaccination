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
@Table(name = "vaccine_news_type")
public class VaccineNewsType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NEWS_TYPE_ID", nullable = false, length = 36)
    private long id;

    @Column(name = "DESCRIPTION", length = 10)
    private String description;

    @Column(name = "NEWS_TYPE_NAME", length = 50)
    private String newsTypeName;

    @OneToMany(mappedBy = "newsType")
    @Builder.Default
    private Set<VaccineNew> vaccineNews = new LinkedHashSet<>();

}