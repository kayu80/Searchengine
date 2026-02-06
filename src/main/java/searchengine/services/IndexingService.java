package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.models.Site;
import searchengine.repositories.SiteRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IndexingService {

    @Autowired
    private SiteRepository siteRepository;

    private ExecutorService executorService;

    public void startIndexing() {

    }

    public void stopIndexing() {

    }

    public void updateSiteStatus(Site site, String status) {

    }

}