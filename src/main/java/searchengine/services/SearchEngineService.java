package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.SearchQueryDTO;
import searchengine.dto.SearchResult;
import searchengine.model.*;
import searchengine.repository.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchEngineService {

    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    private final ThreadPoolTaskExecutor taskExecutor;


    public void startIndexing() {
        List<Site> sites = sitesList.getSites();
        for (Site site : sites) {
            taskExecutor.execute(() -> indexSite(site));
        }
    }

    private void indexSite(Site site) {

    }

    public List<SearchResult> search(String query) {

        return Collections.emptyList();
    }


}