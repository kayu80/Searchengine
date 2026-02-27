package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchResult;
import searchengine.models.Lemma;
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

    public List<SearchResult> performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Map<String, Integer> queryLemmas = lemmatizationService.normalizeWords(query);
            Set<String> lemmaSet = queryLemmas.keySet();

            if (lemmaSet.isEmpty()) {
                return Collections.emptyList();
            }

            List<Page> pages = pageRepository.findByLemmas(lemmaSet);
            List<SearchResult> results = new ArrayList<>();

            for (Page page : pages) {
                double relevance = calculateRelevance(page, lemmaSet, queryLemmas);
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

            return results.stream()
                    .sorted(Comparator.comparingDouble(SearchResult::getRelevance).reversed())
                    .limit(MAX_RESULTS)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при выполнении поиска для запроса '{}': {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private double calculateRelevance(Page page, Set<String> queryLemmas, Map<String, Integer> queryLemmaFrequencies) {
        List<Index> indexes = indexRepository.findByPageId(page.getId());
        Map<String, Integer> pageLemmas = indexes.stream()
                .collect(Collectors.toMap(
                        index -> index.getLemma().getLemma(), // Исправлено: получаем строку леммы
                        Index::getRank,
                        Integer::sum
                ));

        double totalRelevance = 0.0;
        int totalQueryWeight = queryLemmaFrequencies.values().stream().mapToInt(Integer::intValue).sum();

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
            return doc.title().isEmpty() ? "Без заголовка" : doc.title();
        } catch (Exception e) {
            log.warn("Не удалось извлечь заголовок из контента: {}", e.getMessage());
            return "Без заголовка";
        }
    }
}
