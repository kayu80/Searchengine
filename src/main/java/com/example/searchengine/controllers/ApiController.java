package com.example.searchengine.controllers;

import com.example.searchengine.dto.SearchQueryDTO;
import com.example.searchengine.dto.SearchResult;
import com.example.searchengine.dto.statistics.StatisticsResponse;
import com.example.searchengine.services.SearchEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearchEngineService searchEngineService;    @Autowired
    public ApiController(SearchEngineService searchEngineService) {
        this.searchEngineService = searchEngineService;
    }

    /**
     * Метод запуска процесса индексации.
     */
    @PostMapping("/startIndexing")
    public ResponseEntity<Boolean> startIndexing() {
        searchEngineService.startIndexing();
        return ResponseEntity.ok(true);
    }

    /**
     * Метод остановки процесса индексации.
     */
    @PostMapping("/stopIndexing")
    public ResponseEntity<Boolean> stopIndexing() {
        searchEngineService.stopIndexing();
        return ResponseEntity.ok(true);
    }

    /**
     * Поиск документов по запросу.
     *
     * @param query объект с параметрами поиска
     * @return список найденных документов
     */
    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(@Valid @RequestBody SearchQueryDTO query) {
        List<SearchResult> results = searchEngineService.search(query.getQuery());
        return ResponseEntity.ok(results);
    }

    /**
     * Получение общей статистики индексации.
     */
    @GetMapping("/stats")
    public ResponseEntity<StatisticsResponse> getStats() {
        StatisticsResponse stats = searchEngineService.getStats();
        return ResponseEntity.ok(stats);
    }
}