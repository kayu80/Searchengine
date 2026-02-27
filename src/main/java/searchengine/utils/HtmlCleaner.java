package searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlCleaner {


    public static String cleanHtml(String html) {
        Document document = Jsoup.parse(html);
        return document.body().text();
    }
}