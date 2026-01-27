package com.example.searchengine.repository;

import com.example.searchengine.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {

}