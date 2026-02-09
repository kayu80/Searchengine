package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.models.Page;
import searchengine.models.Site;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class IndexingService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final HttpClient httpClient;

    private ExecutorService executorService;
    private static final int MAX_DEPTH = 3;


    public IndexingService(
            SiteRepository siteRepository,
            PageRepository pageRepository
    ) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void startIndexing() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(4);
        }

        List<Site> sites = siteRepository.findAllByStatus("ACTIVE");
        for (Site site : sites) {
            executorService.submit(() -> crawlAndIndexSite(site));
        }
    }

    public void stopIndexing() {
        if (executorService != null) {
            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Индексные задачи не завершились за 60 секунд.");
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateSiteStatus(Site site, String status) {
        site.setStatus(status);
        site.setLastUpdated(LocalDateTime.now());
        siteRepository.save(site);
    }

    private void crawlAndIndexSite(Site site) {
        String baseUrl = site.getUrl();
        Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
        Queue<Map.Entry<String, Integer>> urlQueue = new ConcurrentLinkedQueue<>();

        urlQueue.add(new AbstractMap.SimpleEntry<>(baseUrl, 0));

        while (!urlQueue.isEmpty()) {
            Map.Entry<String, Integer> entry = urlQueue.poll();
            String url = entry.getKey();
            int depth = entry.getValue();

            if (depth > MAX_DEPTH || visitedUrls.contains(url)) {
                continue;
            }

            visitedUrls.add(url);

            try {
                String html = fetchPage(url);
                if (html == null) continue;

                Document doc = Jsoup.parse(html, url);
                String title = doc.title();
                String content = doc.text();

                Page page = new Page();
                page.setSite(site);
                page.setUrl(url);
                page.setTitle(title);
                page.setContent(content);
                page.setCreated(LocalDateTime.now());
                pageRepository.save(page);

                if (depth < MAX_DEPTH) {
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String href = link.attr("abs:href");
                        if (href != null && href.startsWith(baseUrl)) {
                            urlQueue.add(new AbstractMap.SimpleEntry<>(href, depth + 1));
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Ошибка при обработке URL " + url + ": " + e.getMessage());
            }
        }

        updateSiteStatus(site, "INDEXED");
    }

    private String fetchPage(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("HTTP " + response.statusCode() + " для URL: " + url);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Не удалось загрузить страницу " + url + ": " + e.getMessage());
            return null;
        }
    }
}
