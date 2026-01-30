package com.example.searchengine.repository;

import com.example.searchengine.model.Index;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexRepository extends JpaRepository<Index, Long> {
    // Дополните дополнительными методами, если нужны специфичные запросы
}