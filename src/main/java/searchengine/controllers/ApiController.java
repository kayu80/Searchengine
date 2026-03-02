package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.search.SearchQuery;
import searchengine.dto.search.SearchResult;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final IndexingService indexingService;
    private final SearchService searchService;

    public ApiController(IndexingService indexingService, SearchService searchService) {
        this.indexingService = indexingService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse stats = indexingService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/start-indexing")
    public ResponseEntity<Void> startIndexing() {
        indexingService.startIndexing();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/stop-indexing")
    public ResponseEntity<Void> stopIndexing() {
        indexingService.stopIndexing();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(@RequestBody SearchQuery query) {
        if (query == null || query.getQuery() == null || query.getQuery().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<SearchResult> results;

        // Проверяем, есть ли параметры пагинации в запросе
        if (query.getOffset() != null || query.getLimit() != null) {
            // Используем метод с пагинацией
            results = searchService.performSearch(
                    query.getQuery(),
                    query.getOffset(),
                    query.getLimit()
            );
        } else {
            // Используем метод без пагинации
            results = searchService.performSearch(query.getQuery());
        }

        return ResponseEntity.ok(results);
    }
}