package com.example.searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailedStatisticsItem {

    private String url;              // Адрес сайта
    private String name;             // Название сайта
    private String status;           // Текущий статус индексации
    private long statusTime;         // Время последнего обновления статуса
    private String error;            // Информация об ошибке, если была
    private int pages;               // Количество проиндексированных страниц
    private int lemmas;              // Количество извлечённых уникальных лемм
}