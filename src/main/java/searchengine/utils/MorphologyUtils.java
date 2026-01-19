package searchengine.utils;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorphologyUtils {

    private static final LuceneMorphology morphology;

    static {
        try {
            morphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize morphology engine.", e);
        }
    }

    public static String cleanHtmlContent(Document document) {
        return document.text();
    }

    public static Map<String, Integer> extractLemmas(String text) {
        Map<String, Integer> lemmasFrequency = new HashMap<>();
        String cleanedText = cleanHtmlContent(Jsoup.parse(text)).toLowerCase();

        String[] words = cleanedText.split("\\W+");
        for (String word : words) {
            List<String> normalForms = morphology.getNormalForms(word);
            if (!normalForms.isEmpty()) {
                String normalizedWord = normalForms.get(0);
                lemmasFrequency.merge(normalizedWord, 1, Integer::sum);
            }
        }
        return lemmasFrequency;
    }

    public static String generateSnippet(String originalText, List<String> keywords) {
        StringBuilder snippet = new StringBuilder();
        String[] tokens = originalText.split("\\W+");
        for (String token : tokens) {
            if (keywords.contains(token)) {
                snippet.append("<b>").append(token).append("</b>");
            } else {
                snippet.append(token);
            }
            snippet.append(" ");
        }
        return snippet.toString().trim();
    }
}