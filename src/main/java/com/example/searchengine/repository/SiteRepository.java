package com.example.searchengine.repository;

import com.example.searchengine.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
    // Определите специальные методы для работы с сайтами
}