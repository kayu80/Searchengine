package com.example.searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsData {

    private TotalStatistics total;      // Общая статистика по всей системе
    private List<DetailedStatisticsItem> detailed; // Список деталей по каждому сайту
}