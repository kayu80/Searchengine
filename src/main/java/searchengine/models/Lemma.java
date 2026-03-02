package searchengine.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lemmas")
@Data
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lemma", unique = true, nullable = false)
    private String lemma;

    @Column(name = "frequency")
    private Integer frequency;
}
