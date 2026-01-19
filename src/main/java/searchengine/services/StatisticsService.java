package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.Site;
import searchengine.repository.SiteRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SiteRepository siteRepository;

    public StatisticsResponse getStatistics() {
        List<Site> allSites = siteRepository.findAll();
        List<StatisticsData> details = allSites.stream()
                .map(this::convertToStatsData)
                .collect(Collectors.toList());

        StatisticsResponse response = new StatisticsResponse();
        response.setDetails(details);
        return response;
    }

    private StatisticsData convertToStatsData(Site site) {

        return new StatisticsData();
    }

}