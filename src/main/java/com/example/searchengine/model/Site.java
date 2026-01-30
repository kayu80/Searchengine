package com.example.searchengine.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "site")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String url; // Основной URL сайта

    @Column(length = 255, nullable = false)
    private String name; // Имя сайта

    @Column(length = 255, nullable = false)
    private String status; // Текущий статус индексации

    @Column(nullable = false)
    private LocalDateTime statusTime; // Дата последнего обновления статуса

    @Column
    private String lastError; // Сообщение последней ошибки, если возникла
}