package com.example.searchengine.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lemma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lemma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(length = 255, nullable = false)
    private String lemma; // Само слово (лемма)

    @Column(nullable = false)
    private Integer frequency; // Частота появления на сайте
}