package com.example.searchengine.model;

import lombok.*;

import javax.persistence.*;

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
    private String path;

    @Column(nullable = false)
    private Integer code;

    @Lob
    @Column(nullable = false)
    private String content;
}