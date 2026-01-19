package searchengine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.SearchQueryDTO;
import searchengine.dto.StatisticsResponse;
import searchengine.dto.statistics.StatisticsData;
import searchengine.services.SearchEngineService;
import searchengine.services.StatisticsService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearchEngineService searchEngineService;
    private final StatisticsService statisticsService;

    public ApiController(SearchEngineService searchEngineService, StatisticsService statisticsService) {
        this.searchEngineService = searchEngineService;
        this.statisticsService = statisticsService;
    }

    @PostMapping("/startIndexing")
    public ResponseEntity<?> startIndexing() {
        if (!searchEngineService.isIndexingInProgress()) {
            searchEngineService.startIndexing();
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("result", false, "error", "Индексация уже запущена"));
        }
    }

    @PostMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing() {
        if (searchEngineService.isIndexingInProgress()) {
            searchEngineService.stopIndexing();
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("result", false, "error", "Индексация не запущена"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@Valid SearchQueryDTO searchQuery) {
        var results = searchEngineService.search(searchQuery.getQuery(), searchQuery.getOffset(), searchQuery.getLimit());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        StatisticsData stats = statisticsService.getStatistics();
        return ResponseEntity.ok(new StatisticsResponse(stats));
    }
}