package com.example.searchengine.services;

import com.example.searchengine.config.SitesList;
import com.example.searchengine.dto.SearchResult;
import com.example.searchengine.dto.statistics.StatisticsResponse;
import com.example.searchengine.model.Index;
import com.example.searchengine.model.Lemma;
import com.example.searchengine.model.Page;
import com.example.searchengine.model.Site;
import com.example.searchengine.repository.IndexRepository;
import com.example.searchengine.repository.LemmaRepository;
import com.example.searchengine.repository.PageRepository;
import com.example.searchengine.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Начинает процесс индексации.
     */
    public synchronized void startIndexing() {
        if (!isIndexing.getAndSet(true)) { // Проверяем состояние перед началом
            List<Site> sites = sitesList.getSites();
            for (Site site : sites) {
                taskExecutor.execute(() -> indexSite(site)); // Запуск индексации каждого сайта параллельно
            }
        }
    }

    /**
     * Останавливает процесс индексации.
     */
    public synchronized void stopIndexing() {
        isIndexing.set(false);
    }

    /**
     * Осуществляет поиск по базе данных.
     */
    public List<SearchResult> search(String query) {
        // TO DO: Реализация поиска по данным
        return new ArrayList<>();
    }

    /**
     * Индексирует отдельный сайт.
     */
    protected void indexSite(Site site) {
        // TO DO: Реализуйте здесь логику индексации конкретного сайта
    }

    /**
     * Проверяет, идёт ли сейчас индексация.
     */
    public boolean isIndexingInProgress() {
        return isIndexing.get();
    }

    public StatisticsResponse getStats() {
    }
}