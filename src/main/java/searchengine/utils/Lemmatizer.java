package searchengine.utils;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Lemmatizer {

    private final LuceneMorphology morphology;

    public Lemmatizer() throws IOException {
        morphology = new RussianLuceneMorphology();
    }

    public String getLemma(String word) {
        List<String> normalForms = morphology.getNormalForms(word);
        return !normalForms.isEmpty() ? normalForms.get(0) : word;
    }

    public String lemmatizeText(String text) {
        StringBuilder sb = new StringBuilder();
        String[] words = text.split("\\s+");
        for (String word : words) {
            sb.append(getLemma(word)).append(" ");
        }
        return sb.toString().trim();
    }
}