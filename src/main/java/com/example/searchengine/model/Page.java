package com.example.searchengine.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "page")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false)
    private String path; // URL страницы

    @Column(nullable = false)
    private Integer code; // HTTP-код ответа страницы

    @Lob
    @Column(nullable = false)
    private String content; // Содержимое страницы
}