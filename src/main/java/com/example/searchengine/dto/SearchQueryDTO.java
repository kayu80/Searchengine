package com.example.searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryDTO {
    private String query;          // Запрашиваемый текст
    private int offset;            // Смещение результата
    private int limit;             // Лимит выводимых записей
}