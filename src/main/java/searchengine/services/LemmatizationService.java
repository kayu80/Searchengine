package searchengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LemmatizationService {

    private static final Logger log = LoggerFactory.getLogger(LemmatizationService.class);
    private final Map<String, String> lemmaCache = new ConcurrentHashMap<>();
    private static final Pattern CLEAN_PATTERN = Pattern.compile("[^а-яА-ЯёЁа-zA-Z0-9\\s\\-']");

    public Map<String, Integer> normalizeWords(String input) {
        Map<String, Integer> lemmas = new HashMap<>();

        if (input == null || input.trim().isEmpty()) {
            return lemmas;
        }

        String cleanedInput = CLEAN_PATTERN.matcher(input.toLowerCase()).replaceAll(" ");
        String[] words = cleanedInput.split("\\s+");

        for (String word : words) {
            if (word.isEmpty()) continue;
            String lemma = lemmaCache.computeIfAbsent(word, this::getLemmaFromAnalyzer);
            lemmas.put(lemma, lemmas.getOrDefault(lemma, 0) + 1);
        }
        return lemmas;
    }

    private String getLemmaFromAnalyzer(String word) {
        try {
            // Базовая эвристика для русского языка
            if (word.endsWith("ы") || word.endsWith("и")) {
                return word.substring(0, word.length() - 1); // мн. ч. → ед. ч.
            } else if (word.endsWith("ой") || word.endsWith("ий")) {
                return word.substring(0, word.length() - 2) + "ый";
            } else if (word.endsWith("ая") || word.endsWith("яя")) {
                return word.substring(0, word.length() - 2) + "ая";
            } else if (word.endsWith("ого") || word.endsWith("его")) {
                return word.substring(0, word.length() - 3) + "ый";
            } else if (word.endsWith("ому") || word.endsWith("ему")) {
                return word.substring(0, word.length() - 3) + "ому";
            }
            // Для английского: убираем окончание -s
            else if (word.endsWith("s") && word.length() > 3) {
                return word.substring(0, word.length() - 1);
            }
            return word; // если не подходит ни одно правило
        } catch (Exception e) {
            log.warn("Ошибка при лемматизации слова '{}': {}", word, e.getMessage());
            return word;
        }
    }
}
