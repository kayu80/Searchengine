package searchengine.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "site")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date statusTime;

    @Column(length = 255, nullable = false)
    private String url;

    @Column(length = 255, nullable = false)
    private String name;
}