package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuery {
    private String query;
    private Integer offset = 0;
    private Integer limit = 20;
    private String site; // опционально, для поиска по конкретному сайту
}