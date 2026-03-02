package searchengine.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "indexes")
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
