package com.example.searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalStatistics {

    private int sites;                 // Общее количество сайтов
    private int pages;                 // Общее число проиндексированных страниц
    private int lemmas;                // Общее число уникальных лемм
    private boolean indexing;          // Статус текущего процесса индексации
}