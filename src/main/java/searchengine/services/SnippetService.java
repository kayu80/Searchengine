package searchengine.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SnippetService {

    private static final int PREVIEW_LENGTH = 150;
    private static final int POST_VIEW_LENGTH = 100;

    public String generateSnippet(String content, Set<String> lemmas) {
        if (content == null || content.isEmpty() || lemmas.isEmpty()) {
            return "";
        }

        String lowerContent = content.toLowerCase();
        int bestPosition = -1;
        String bestLemma = "";

        for (String lemma : lemmas) {
            int position = lowerContent.indexOf(lemma.toLowerCase());
            if (position != -1 && (bestPosition == -1 || position < bestPosition)) {
                bestPosition = position;
                bestLemma = lemma;
            }
        }

        if (bestPosition == -1) {
            return shortenText(content, PREVIEW_LENGTH + POST_VIEW_LENGTH);
        }

        int start = Math.max(0, bestPosition - PREVIEW_LENGTH);
        int end = Math.min(content.length(), bestPosition + bestLemma.length() + POST_VIEW_LENGTH);

        String snippet = content.substring(start, end);
        snippet = highlightLemma(snippet, bestLemma);

        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < content.length()) {
            snippet = snippet + "...";
        }

        return snippet;
    }

    private String shortenText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private String highlightLemma(String text, String lemma) {
        Pattern pattern = Pattern.compile("(" + Pattern.quote(lemma) + ")", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll("<mark>$1</mark>");
    }
}
