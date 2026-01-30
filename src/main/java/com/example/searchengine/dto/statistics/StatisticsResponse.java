package com.example.searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {

    private boolean result;            // Результат операции (true, если успешна)
    private StatisticsData statistics; // Детальная информация о статистике
}