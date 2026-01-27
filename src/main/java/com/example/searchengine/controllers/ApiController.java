package com.example.searchengine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.searchengine.services.SearchEngineService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SearchEngineService searchEngineService;

    @Autowired
    public ApiController(SearchEngineService searchEngineService) {
        this.searchEngineService = searchEngineService;
    }

    @PostMapping("/startIndexing")
    public Boolean startIndexing() {
        searchEngineService.startIndexing();
        return true;
    }

    @PostMapping("/stopIndexing")
    public Boolean stopIndexing() {
        searchEngineService.stopIndexing();
        return true;
    }

    @GetMapping("/search/{query}")
    public List<SearchResult> search(
            @PathVariable String query
    ) {
        return searchEngineService.search(query);
    }
    @GetMapping("/stats")
    public StatisticsResponse getStats() {
        return searchEngineService.getStats();
    }
}