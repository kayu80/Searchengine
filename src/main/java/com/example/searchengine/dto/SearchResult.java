package com.example.searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String uri;            // URI найденной страницы
    private String title;          // Заголовок страницы
    private String snippet;        // Краткое содержание страницы
    private float relevance;       // Релевантность страницы относительно запроса
}