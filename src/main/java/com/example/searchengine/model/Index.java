package com.example.searchengine.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "index_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Index {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lemma_id", nullable = false)
    private Lemma lemma;

    @Column(nullable = false)
    private Float rank; // Значение ранга для каждой пары "страница + слово"
}