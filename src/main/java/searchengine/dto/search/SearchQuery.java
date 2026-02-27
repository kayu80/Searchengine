package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchQuery {
    private String query;
}