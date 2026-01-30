package com.example.searchengine.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorphologyUtils {

    private static final Analyzer ANALYZER = new StandardAnalyzer(); // Анализатор для стандартной нормализации

    /**
     * Очищает HTML-разметку и извлекает чистый текст из документа.
     *
     * @param htmlDocument HTML-документ
     * @return Чистый текст документа
     */
    public static String cleanHtmlContent(Document htmlDocument) {
        return htmlDocument.body().text(); // Используем body для чистки разметки
    }

    /**
     * Преобразует строку текста в нормализованные формы (леммы) с подсчетом частоты.
     *
     * @param text Входной текст
     * @return Карта лемм и их частот
     * @throws IOException Возможные ошибки ввода-вывода
     */
    public static Map<String, Integer> extractLemmas(String text) throws IOException {
        Map<String, Integer> lemmasFrequency = new HashMap<>();
        TokenStream stream = ANALYZER.tokenStream("", new StringReader(text));
        CharTermAttribute termAttr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();

        while (stream.incrementToken()) {
            String lemma = termAttr.toString();
            lemmasFrequency.merge(lemma, 1, Integer::sum); // Инкрементируем частоту слова
        }
        stream.end();
        stream.close();
        return lemmasFrequency;
    }

    /**
     * Генерирует сниппет текста с подсветкой ключевых слов.
     *
     * @param originalText Исходный текст
     * @param keywords Список ключевых слов
     * @return Формированный сниппет с ключевыми словами
     */
    public static String generateSnippet(String originalText, List<String> keywords) {
        StringBuilder snippet = new StringBuilder();
        String[] tokens = originalText.split("\\s+"); // Разбиваем текст на отдельные слова
        for (String token : tokens) {
            if (keywords.contains(token)) {
                snippet.append("<b>").append(token).append("</b> "); // Выделяем ключевые слова полужирным
            } else {
                snippet.append(token).append(" ");
            }
        }
        return snippet.toString().trim();
    }
}