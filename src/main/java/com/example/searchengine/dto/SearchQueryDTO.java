package com.example.searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryDTO {

    @NotBlank(message = "Запрос не может быть пустым.")
    private String query;          // Сам поисковый запрос
    private int offset;            // Смещение начала выборки
    private int limit;             // Ограничение количества результатов
}