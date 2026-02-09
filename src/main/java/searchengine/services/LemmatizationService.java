package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LemmatizationService {

    private final LuceneMorphology morphAnalyzer;

    public LemmatizationService() throws IOException {
        morphAnalyzer = new RussianLuceneMorphology();
    }

    public Map<String, Integer> normalizeWords(String input) {
        Map<String, Integer> lemmas = new HashMap<>();

        if (input == null || input.trim().isEmpty()) {
            return lemmas;
        }

        String[] words = input.toLowerCase()
                .replaceAll("[^а-яА-ЯёЁа-zA-Z0-9\\s]", " ")
                .split("\\s+");

        for (String word : words) {
            if (word.isEmpty()) continue;

            List<String> wordForms = morphAnalyzer.getNormalForms(word);
            if (!wordForms.isEmpty()) {
                String lemma = wordForms.get(0);
                lemmas.put(lemma, lemmas.getOrDefault(lemma, 0) + 1);
            } else {
                lemmas.put(word, lemmas.getOrDefault(word, 0) + 1);
            }
        }

        return lemmas;
    }
}
