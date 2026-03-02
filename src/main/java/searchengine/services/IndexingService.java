package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private static final Logger log = LoggerFactory.getLogger(IndexingService.class);
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmatizationService lemmatizationService;
    private final SitesList sitesList;

    private final AtomicBoolean isIndexing = new AtomicBoolean(false);
    private ExecutorService executor;
    private final Map<Long, Future<?>> siteTasks = new ConcurrentHashMap<>();

    @Transactional
    public void startIndexing() {
        if (!isIndexing.compareAndSet(false, true)) {
            log.warn("Индексация уже выполняется");
            return;
        }

        executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new ThreadFactory() {
                    private final AtomicInteger threadCounter = new AtomicInteger(0); // Исправлено: AtomicInteger вместо AtomicBoolean
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("indexing-thread-" + threadCounter.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                }
        );

        try {
            List<Site> sites = siteRepository.findAll();
            if (sites.isEmpty()) {
                // Если сайтов нет в БД, создаем из конфигурации
                for (searchengine.config.Site configSite : sitesList.getSites()) {
                    Site site = new Site();
                    site.setUrl(configSite.getUrl());
                    site.setName(configSite.getName());
                    site.setStatus(Status.INDEXING);
                    site.setStatusTime(System.currentTimeMillis());
                    site.setLastUpdated(LocalDateTime.now());
                    siteRepository.save(site);
                }
                sites = siteRepository.findAll();
            }

            for (Site site : sites) {
                Future<?> future = executor.submit(() -> indexSite(site));
                siteTasks.put(site.getId(), future);
            }

            executor.shutdown();

        } catch (Exception e) {
            log.error("Ошибка при запуске индексации", e);
            isIndexing.set(false);
        }
    }

    public void stopIndexing() {
        isIndexing.set(false);
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }

        // Прерываем все задачи сайтов
        siteTasks.values().forEach(future -> future.cancel(true));
        siteTasks.clear();

        // Обновляем статус сайтов
        List<Site> sites = siteRepository.findAll();
        for (Site site : sites) {
            if (site.getStatus() == Status.INDEXING) {
                site.setStatus(Status.FAILED);
                site.setLastError("Индексация остановлена пользователем");
                site.setLastUpdated(LocalDateTime.now());
                siteRepository.save(site);
            }
        }

        log.info("Индексация остановлена");
    }

    private void indexSite(Site site) {
        try {
            log.info("Начало индексации сайта: {}", site.getUrl());

            site.setStatus(Status.INDEXING);
            site.setLastUpdated(LocalDateTime.now());
            site.setLastError(null);
            site = siteRepository.save(site);

            crawlAndIndexPages(site);

            if (isIndexing.get()) {
                site.setStatus(Status.INDEXED);
                log.info("Индексация сайта завершена: {}", site.getUrl());
            } else {
                site.setStatus(Status.FAILED);
                site.setLastError("Индексация прервана");
            }
            site.setLastUpdated(LocalDateTime.now());
            siteRepository.save(site);

        } catch (Exception e) {
            log.error("Ошибка при индексации сайта: {}", site.getUrl(), e);
            site.setStatus(Status.FAILED);
            site.setLastError(e.getMessage());
            site.setLastUpdated(LocalDateTime.now());
            siteRepository.save(site);
        } finally {
            siteTasks.remove(site.getId());
        }
    }

    private void crawlAndIndexPages(Site site) {
        Set<String> visitedUrls = ConcurrentHashMap.newKeySet(); // Исправлено
        BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>();

        urlQueue.add(site.getUrl());

        while (!urlQueue.isEmpty() && isIndexing.get() && !Thread.currentThread().isInterrupted()) {
            String currentUrl = urlQueue.poll();
            if (currentUrl == null || visitedUrls.contains(currentUrl)) {
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

                // Небольшая задержка, чтобы не перегружать сервер
                Thread.sleep(100);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Прервана индексация страницы {}", currentUrl);
                break;
            } catch (Exception e) {
                log.warn("Не удалось обработать страницу {}: {}", currentUrl, e.getMessage());
            }
        }
    }

    @Transactional
    protected Page fetchAndSavePage(Site site, String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(10000)
                    .userAgent("Mozilla/5.0 (compatible; SearchBot/1.0; +http://example.com/bot)")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .get();

            String path = url.replace(site.getUrl(), "");
            if (path.isEmpty()) path = "/";

            // Проверяем, существует ли уже страница
            Page page = pageRepository.findBySiteIdAndPath(site.getId(), path);
            if (page == null) {
                page = new Page();
                page.setSite(site);
                page.setPath(path);
            }

            page.setContent(doc.html());
            page.setCode(doc.connection().response().statusCode());
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
            String cleanText = Jsoup.parse(page.getContent()).text();
            Map<String, Integer> lemmas = lemmatizationService.normalizeWords(cleanText);

            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemmaText = entry.getKey();
                int frequency = entry.getValue();

                Lemma lemma = lemmaRepository.findByLemma(lemmaText)
                        .orElseGet(() -> {
                            Lemma newLemma = new Lemma();
                            newLemma.setLemma(lemmaText);
                            newLemma.setFrequency(0);
                            return lemmaRepository.save(newLemma);
                        });

                lemma.setFrequency(lemma.getFrequency() + 1);
                lemmaRepository.save(lemma);

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

        index.setId(indexPK);
        index.setPage(page);
        index.setLemma(lemma);
        index.setRank(rank);

        indexRepository.save(index);
    }

    private List<String> extractLinksFromPage(String htmlContent, String baseUrl) {
        List<String> links = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(htmlContent, baseUrl);
            Elements linksElements = doc.select("a[href]");

            for (Element link : linksElements) {
                String href = link.attr("abs:href");
                if (href.startsWith(baseUrl) &&
                        !href.contains("#") &&
                        !href.contains("mailto:") &&
                        !href.contains("javascript:") &&
                        !href.endsWith(".pdf") &&
                        !href.endsWith(".jpg") &&
                        !href.endsWith(".png") &&
                        !href.endsWith(".jpeg") &&
                        !href.endsWith(".gif") &&
                        !href.endsWith(".css") &&
                        !href.endsWith(".js")) {
                    links.add(href);
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка извлечения ссылок: {}", e.getMessage());
        }
        return links;
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics() {
        StatisticsResponse response = new StatisticsResponse();

        TotalStatistics total = new TotalStatistics();
        total.setSites((int) siteRepository.count());
        total.setPages((int) pageRepository.count());
        total.setLemmas((int) lemmaRepository.count());
        total.setIndexing(isIndexing.get());

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sites = siteRepository.findAll();

        for (Site site : sites) {
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setUrl(site.getUrl());
            item.setName(site.getName());
            item.setStatus(site.getStatus().toString());
            item.setStatusTime(site.getStatusTime());
            item.setError(site.getLastError() != null ? site.getLastError() : "");

            long pagesCount = pageRepository.countBySiteId(site.getId());
            item.setPages((int) pagesCount);

            // TODO: добавить подсчет лемм для сайта
            item.setLemmas(0);
            detailed.add(item);
        }

        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);

        response.setResult(true);
        response.setStatistics(data);

        return response;
    }
}