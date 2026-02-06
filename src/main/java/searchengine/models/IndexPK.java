package searchengine.models;

import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexPK {
    private Long pageId;
    private Long lemmaId;
}
