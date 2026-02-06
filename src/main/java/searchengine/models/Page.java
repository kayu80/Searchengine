package searchengine.models;

import javax.persistence.*;
import lombok.*;
import java.util.List; // <-- ДОБАВЬТЕ ЭТУ СТРОКУ

@Entity
@Table(name = "page")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private int code;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<Index> indexes;
}