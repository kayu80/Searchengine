package com.example.searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    private String uri;            // URI документа
    private String title;          // Заголовок документа
    private String snippet;        // Краткий фрагмент содержания документа
    private float relevance;       // Релевантность документа относительно запроса
}