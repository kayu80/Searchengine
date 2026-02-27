package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.search.SearchQuery;
import searchengine.dto.search.SearchResult;
import searchengine.services.SearchService;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("")
    public ResponseEntity<List<SearchResult>> performSearch(@RequestBody SearchQuery query) {
        List<SearchResult> results = searchService.performSearch(query.getQuery());
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}