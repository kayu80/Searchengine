package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.search.SearchResult;
import searchengine.models.Index;
import searchengine.models.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
    private final LemmatizationService lemmatizationService;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final SnippetService snippetService;

    private static final int MAX_RESULTS = 50;
    private static final double MIN_RELEVANCE = 0.001;

    @Transactional(readOnly = true)
    public List<SearchResult> performSearch(String query) {  // Только 1 параметр - query
        return performSearch(query, 0, MAX_RESULTS);  // Вызываем основной метод с параметрами по умолчанию
    }

    @Transactional(readOnly = true)
    public List<SearchResult> performSearch(String query, Integer offset, Integer limit) {  // Полная версия с пагинацией
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        int pageOffset = offset != null ? Math.max(0, offset) : 0;
        int pageLimit = limit != null ? Math.min(limit, MAX_RESULTS) : MAX_RESULTS;

        try {
            Map<String, Integer> queryLemmas = lemmatizationService.normalizeWords(query);
            Set<String> lemmaSet = queryLemmas.keySet();

            if (lemmaSet.isEmpty()) {
                return Collections.emptyList();
            }

            List<Page> pages = pageRepository.findByLemmas(lemmaSet);

            if (pages.isEmpty()) {
                return Collections.emptyList();
            }

            // Загружаем все индексы для найденных страниц одним запросом
            List<Long> pageIds = pages.stream().map(Page::getId).collect(Collectors.toList());
            List<Index> allIndexes = indexRepository.findByPageIds(pageIds);

            // Группируем индексы по страницам
            Map<Long, List<Index>> indexesByPage = allIndexes.stream()
                    .filter(idx -> idx.getLemma() != null)
                    .collect(Collectors.groupingBy(idx -> idx.getPage().getId()));

            List<SearchResult> results = new ArrayList<>();

            for (Page page : pages) {
                List<Index> pageIndexes = indexesByPage.getOrDefault(page.getId(), Collections.emptyList());
                double relevance = calculateRelevance(pageIndexes, lemmaSet, queryLemmas);

                if (relevance >= MIN_RELEVANCE) {
                    String snippet = snippetService.generateSnippet(page.getContent(), lemmaSet);
                    SearchResult result = new SearchResult();
                    result.setUri(page.getSite().getUrl() + page.getPath());
                    result.setTitle(extractTitleFromContent(page.getContent()));
                    result.setSnippet(snippet);
                    result.setRelevance(relevance);
                    results.add(result);
                }
            }

            // Сортируем по релевантности и применяем пагинацию
            return results.stream()
                    .sorted(Comparator.comparingDouble(SearchResult::getRelevance).reversed())
                    .skip(pageOffset)
                    .limit(pageLimit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Ошибка при выполнении поиска для запроса '{}': {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private double calculateRelevance(List<Index> pageIndexes,
                                      Set<String> queryLemmas,
                                      Map<String, Integer> queryLemmaFrequencies) {

        Map<String, Integer> pageLemmas = pageIndexes.stream()
                .filter(index -> index.getLemma() != null && index.getLemma().getLemma() != null)
                .collect(Collectors.toMap(
                        index -> index.getLemma().getLemma(),
                        Index::getRank,
                        Integer::sum
                ));

        double totalRelevance = 0.0;
        int totalQueryWeight = queryLemmaFrequencies.values().stream().mapToInt(Integer::intValue).sum();

        if (totalQueryWeight == 0) {
            return 0;
        }

        for (String lemma : queryLemmas) {
            int queryWeight = queryLemmaFrequencies.getOrDefault(lemma, 1);
            int pageRank = pageLemmas.getOrDefault(lemma, 0);
            double lemmaRelevance = (double) pageRank / (pageLemmas.size() + 1);
            totalRelevance += lemmaRelevance * queryWeight;
        }

        return totalRelevance / totalQueryWeight;
    }

    private String extractTitleFromContent(String content) {
        try {
            Document doc = Jsoup.parse(content);
            String title = doc.title();
            return title.isEmpty() ? "Без заголовка" : title;
        } catch (Exception e) {
            log.warn("Не удалось извлечь заголовок из контента: {}", e.getMessage());
            return "Без заголовка";
        }
    }
}