package searchengine.models;

import javax.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "lemma")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String text;

    @Column(nullable = false)
    private int frequency;

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    private List<Index> indexes;
}