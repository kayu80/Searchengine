package com.example.searchengine.repository;

import com.example.searchengine.model.Lemma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LemmaRepository extends JpaRepository<Lemma, Long> {

}