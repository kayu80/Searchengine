package com.example.searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.searchengine.dto.statistics.StatisticsData;
import com.example.searchengine.dto.statistics.StatisticsResponse;
import com.example.searchengine.model.Site;
import com.example.searchengine.repository.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SiteRepository siteRepository;

    public StatisticsResponse getStatistics() {
        List<Site> allSites = siteRepository.findAll();
        StatisticsData data = prepareStatistics(allSites);
        return new StatisticsResponse(data);
    }

    private StatisticsData prepareStatistics(List<Site> sites) {

        return null;
    }
}