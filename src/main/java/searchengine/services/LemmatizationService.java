package searchengine.services;

import org.springframework.stereotype.Service;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class LemmatizationService {

    private final LuceneMorphology morphAnalyzer;

    public LemmatizationService() throws IOException {
        morphAnalyzer = new RussianLuceneMorphology();
    }

    public Map<String, Integer> normalizeWords(String input) {

        return new HashMap<>();
    }

}