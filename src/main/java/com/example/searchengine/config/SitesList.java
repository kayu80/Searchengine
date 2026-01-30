package com.example.searchengine.config;

import com.example.searchengine.model.Site;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

// Класс для чтения списка сайтов из configuration
@Component
@Getter
@ConfigurationProperties(prefix = "indexing")
public class SitesList {

    private List<Site> sites;

}