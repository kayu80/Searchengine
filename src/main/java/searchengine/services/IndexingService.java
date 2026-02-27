package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.enums.Status;
import searchengine.models.Index;
import searchengine.models.IndexPK;
import searchengine.models.Lemma;
import searchengine.models.Page;
import searchengine.models.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private static final Logger log = LoggerFactory.getLogger(IndexingService.class);
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmatizationService lemmatizationService;

    private volatile boolean isIndexing = false;
    private ExecutorService executor;

    @Transactional
    public void startIndexing() {
        if (isIndexing) {
            log.warn("Индексация уже выполняется");
            return;
        }

        isIndexing = true;
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Site> sites = siteRepository.findAll();
            for (Site site : sites) {
                executor.submit(() -> indexSite(site));
            }
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Прервано выполнение индексации: {}", e.getMessage());
        } finally {
            isIndexing = false;
        }
    }

    public void stopIndexing() {
        isIndexing = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        log.info("Индексация остановлена");
    }

    private void indexSite(Site site) {
        try {
            site.setStatus(Status.INDEXING);
            site.setLastUpdated(LocalDateTime.now());
            siteRepository.save(site);

            crawlAndIndexPages(site);

            site.setStatus(Status.INDEXED);
            site.setLastUpdated(LocalDateTime.now());
            siteRepository.save(site);
        } catch (Exception e) {
            log.error("Ошибка при индексации сайта: {}", site.getUrl(), e);
            site.setStatus(Status.FAILED);
            site.setLastUpdated(LocalDateTime.now());
            siteRepository.save(site);
        }
    }

    private void crawlAndIndexPages(Site site) {
        Set<String> visitedUrls = new HashSet<>();
        Queue<String> urlQueue = new LinkedList<>();

        urlQueue.add(site.getUrl());

        while (!urlQueue.isEmpty() && isIndexing) {
            String currentUrl = urlQueue.poll();

            if (visitedUrls.contains(currentUrl)) {
                continue;
            }

            visitedUrls.add(currentUrl);

            try {
                Page page = fetchAndSavePage(site, currentUrl);
                if (page != null) {
                    indexPageContent(page);

                    List<String> newUrls = extractLinksFromPage(page.getContent(), site.getUrl());
                    for (String url : newUrls) {
                        if (!visitedUrls.contains(url) && url.startsWith(site.getUrl())) {
                            urlQueue.offer(url);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Не удалось обработать страницу {}: {}", currentUrl, e.getMessage());
            }
        }
    }

    private Page fetchAndSavePage(Site site, String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(10000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            Page page = new Page();
            page.setSite(site);
            page.setPath(url.replace(site.getUrl(), ""));
            page.setContent(doc.html());
            page.setCode(200);
            page.setLastUpdated(LocalDateTime.now());

            return pageRepository.save(page);
        } catch (IOException e) {
            log.warn("Ошибка загрузки страницы {}: {}", url, e.getMessage());
            return null;
        }
    }

    @Transactional
    protected void indexPageContent(Page page) {
        try {
            Map<String, Integer> lemmas = lemmatizationService.normalizeWords(page.getContent());

            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemmaText = entry.getKey();
                int frequency = entry.getValue();

                Lemma lemma = lemmaRepository.findByLemma(lemmaText)
                        .orElseGet(() -> {
                            Lemma newLemma = new Lemma();
                            newLemma.setLemma(lemmaText);
                            return lemmaRepository.save(newLemma);
                        });

                updateIndex(page, lemma, frequency);
            }
        } catch (Exception e) {
            log.error("Ошибка индексации содержимого страницы {}: {}", page.getPath(), e.getMessage());
        }
    }

    @Transactional
    protected void updateIndex(Page page, Lemma lemma, int rank) {
        IndexPK indexPK = new IndexPK(page.getId(), lemma.getId());

        Index index = indexRepository.findById(indexPK)
                .orElse(new Index());

        index.setPage(page);
        index.setLemma(lemma);
        index.setRank(rank);

        indexRepository.save(index);
    }

    private List<String> extractLinksFromPage(String htmlContent, String baseUrl) {
        List<String> links = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(htmlContent);
            Elements linksElements = doc.select("a[href]");

            for (Element link : linksElements) {
                String href = link.attr("abs:href"); // абсолютный URL
                if (href.startsWith(baseUrl) && !href.contains("#") && !href.contains("mailto:")) {
                    links.add(href);
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка извлечения ссылок: {}", e.getMessage());
        }
        return links;
    }

    public StatisticsResponse getStatistics() {
        long totalSites = siteRepository.count();
        long indexedPages = pageRepository.count();
        long lemmas = lemmaRepository.count();

        StatisticsResponse stats = new StatisticsResponse();
        stats.setTotalSites(totalSites);
        stats.setIndexedPages(indexedPages);
        stats.setLemmas(lemmas);

        return stats;
    }
}
