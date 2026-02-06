package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchQuery;
import searchengine.dto.search.SearchResult;
import searchengine.repositories.PageRepository;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    private PageRepository pageRepository;

    public List<SearchResult> performSearch(SearchQuery query) {

        return null;
    }

}