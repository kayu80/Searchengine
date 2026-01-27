package com.example.searchengine.model;

import lombok.*;

import javax.persistence.*;

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
    private String lemma; // Нормализованная форма слова

    @Column(nullable = false)
    private Integer frequency; // Частота встречаемости в документах
}