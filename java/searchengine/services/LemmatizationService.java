package searchengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LemmatizationService {

    private static final Logger log = LoggerFactory.getLogger(LemmatizationService.class);
    private final Map<String, String> lemmaCache = new ConcurrentHashMap<>();
    private static final Pattern WORD_PATTERN = Pattern.compile("[а-яА-ЯёЁa-zA-Z]+(?:-[а-яА-ЯёЁa-zA-Z]+)?");
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[^а-яА-ЯёЁa-zA-Z\\-\\s]");

    // Русские окончания для базовой лемматизации
    private static final Map<String, List<String>> RUSSIAN_SUFFIXES = new HashMap<>();
    static {
        // Существительные
        RUSSIAN_SUFFIXES.put("а", Arrays.asList("ы", "е", "у", "ой", "ами"));
        RUSSIAN_SUFFIXES.put("я", Arrays.asList("и", "е", "ю", "ей", "ями"));
        RUSSIAN_SUFFIXES.put("ь", Arrays.asList("и", "ю", "ем", "ях"));
        RUSSIAN_SUFFIXES.put("й", Arrays.asList("я", "ю", "ем", "и"));

        // Прилагательные
        RUSSIAN_SUFFIXES.put("ый", Arrays.asList("ого", "ому", "ым", "ом", "ая", "ую"));
        RUSSIAN_SUFFIXES.put("ий", Arrays.asList("его", "ему", "им", "ем", "яя", "юю"));
        RUSSIAN_SUFFIXES.put("ой", Arrays.asList("ого", "ому", "ым", "ом", "ая", "ую"));

        // Глаголы
        RUSSIAN_SUFFIXES.put("ть", Arrays.asList("л", "ла", "ло", "ли", "ет", "ют", "ит"));
    }

    public Map<String, Integer> normalizeWords(String input) {
        Map<String, Integer> lemmas = new HashMap<>();

        if (input == null || input.trim().isEmpty()) {
            return lemmas;
        }

        String cleanedInput = PUNCTUATION_PATTERN.matcher(input.toLowerCase()).replaceAll(" ");

        BreakIterator wordIterator = BreakIterator.getWordInstance(new Locale("ru"));
        wordIterator.setText(cleanedInput);

        int start = wordIterator.first();
        for (int end = wordIterator.next(); end != BreakIterator.DONE; start = end, end = wordIterator.next()) {
            String word = cleanedInput.substring(start, end).trim();
            if (word.isEmpty() || !WORD_PATTERN.matcher(word).matches()) {
                continue;
            }

            String lemma = lemmaCache.computeIfAbsent(word, this::getLemma);
            lemmas.put(lemma, lemmas.getOrDefault(lemma, 0) + 1);
        }

        return lemmas;
    }

    private String getLemma(String word) {
        if (word.length() < 3) {
            return word;
        }

        // Пытаемся найти базовую форму, убирая окончания
        for (Map.Entry<String, List<String>> entry : RUSSIAN_SUFFIXES.entrySet()) {
            String baseSuffix = entry.getKey();
            for (String suffix : entry.getValue()) {
                if (word.endsWith(suffix)) {
                    String candidate = word.substring(0, word.length() - suffix.length()) + baseSuffix;
                    // Проверяем, что кандидат похож на реальное слово
                    if (candidate.length() >= 2) {
                        return candidate;
                    }
                }
            }
        }

        // Английские слова - просто убираем 's' в конце
        if (word.endsWith("'s")) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 3) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }
}