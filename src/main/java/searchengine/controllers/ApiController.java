package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private IndexingService indexingService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse stats = indexingService.getStatistics();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping("/start-indexing")
    public ResponseEntity<Void> startIndexing() {
        indexingService.startIndexing();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/stop-indexing")
    public ResponseEntity<Void> stopIndexing() {
        indexingService.stopIndexing();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}