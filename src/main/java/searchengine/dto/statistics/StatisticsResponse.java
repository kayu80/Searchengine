package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private boolean result;
    private StatisticsData statistics;
    private long totalSites;
    private long indexedPages;
    private long lemmas;
}