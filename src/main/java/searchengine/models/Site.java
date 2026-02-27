package searchengine.models;

import searchengine.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "site")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Page> pages;
}
