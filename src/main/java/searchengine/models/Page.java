package searchengine.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "code")
    private Integer code;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
