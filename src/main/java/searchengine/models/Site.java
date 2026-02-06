package searchengine.models;

import searchengine.enums.Status;
import javax.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "site")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private long statusTime;

    @Column(length = 1024)
    private String lastError;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Page> pages;
}