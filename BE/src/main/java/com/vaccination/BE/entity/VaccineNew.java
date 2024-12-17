package com.vaccination.BE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vaccine_news")
public class VaccineNew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NEWS_ID", nullable = false, length = 36)
    private long id;

    @Column(name = "CONTENT", length = 1000)
    private String content;

    @Column(name = "PREVIEW", length = 1000)
    private String preview;

    @Column(name = "TITLE", length = 300)
    private String title;
    @Column(name = "POST_DATE")
    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEWS_TYPE_ID")
    private VaccineNewsType newsType;

}