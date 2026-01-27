package com.example.searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.example.searchengine.config.SitesList;
import com.example.searchengine.dto.SearchResult;
import com.example.searchengine.model.*;
import com.example.searchengine.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class SearchEngineService {

    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    private AtomicBoolean isIndexing = new AtomicBoolean(false);

    public void startIndexing() {
        if (!isIndexing.get()) {
            isIndexing.set(true);
            List<Site> sites = sitesList.getSites();
            for (Site site : sites) {
                taskExecutor.execute(() -> indexSite(site));
            }
        }
    }

    public void stopIndexing() {
        isIndexing.set(false);
    }

    public List<SearchResult> search(String query) {
        // TODO: Выполнить поиск по базе данных и сформировать результаты
        return new ArrayList<>(); // Пустая заглушка
    }

    private void indexSite(Site site) {

    }

    public boolean isIndexingInProgress() {
        return isIndexing.get();
    }
}