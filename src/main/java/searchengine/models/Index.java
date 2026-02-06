package searchengine.models;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "index")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index {
    @EmbeddedId
    private IndexPK id;

    @ManyToOne
    @MapsId("pageId")
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToOne
    @MapsId("lemmaId")
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;

    @Column(nullable = false)
    private int rank;
}