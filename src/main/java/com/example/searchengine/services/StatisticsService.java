package com.example.searchengine.services;

import com.example.searchengine.dto.statistics.StatisticsData;
import com.example.searchengine.dto.statistics.StatisticsResponse;
import com.example.searchengine.model.Site;
import com.example.searchengine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SiteRepository siteRepository;

    /**
     * Формирует полную статистику.
     */
    public StatisticsResponse getStatistics() {
        List<Site> allSites = siteRepository.findAll();
        StatisticsData data = prepareStatistics(allSites);
        return new StatisticsResponse(data);
    }

    /**
     * Подготавливает статистику на основе списка сайтов.
     */
    private StatisticsData prepareStatistics(List<Site> sites) {
        // TO DO: Реализуйте подготовку статистики
        return null;
    }
}