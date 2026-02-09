package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchQuery;
import searchengine.dto.search.SearchResult;
import searchengine.models.Page;
import searchengine.repositories.PageRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmatizationService lemmatizationService;

    public List<SearchResult> performSearch(SearchQuery query) {
        String searchText = query.getQuery();
        if (searchText == null || searchText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Integer> queryLemmas = lemmatizationService.normalizeWords(searchText);

        List<Page> pages = pageRepository.findByLemmas(queryLemmas.keySet());

        List<SearchResult> results = pages.stream()
                .map(page -> {
                    int relevance = calculateRelevance(page, queryLemmas);
                    return new SearchResult(
                            page.getId(),
                            page.getUrl(),
                            page.getContent(),
                            relevance
                    );
                })
                .filter(result -> result.getRelevance() > 0)
                .sorted(Comparator.comparing(SearchResult::getRelevance).reversed())
                .collect(Collectors.toList());

        return results;
    }

    private int calculateRelevance(Page page, Map<String, Integer> queryLemmas) {

        Map<String, Integer> pageLemmas = lemmatizationService.normalizeWords(page.getContent());
        int relevance = 0;

        for (Map.Entry<String, Integer> entry : queryLemmas.entrySet()) {
            String lemma = entry.getKey();
            int queryWeight = entry.getValue();

            if (pageLemmas.containsKey(lemma)) {
                int pageFreq = pageLemmas.get(lemma);
                relevance += queryWeight * pageFreq;
            }
        }

        return relevance;
    }
}
