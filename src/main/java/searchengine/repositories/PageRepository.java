package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.models.Page;
import java.util.List;
import java.util.Set;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT DISTINCT p FROM Page p " +
            "JOIN Index i ON p.id = i.page.id " +
            "JOIN Lemma l ON i.lemma.id = l.id " +
            "WHERE l.lemma IN :lemmas")
    List<Page> findByLemmas(@Param("lemmas") Set<String> lemmaSet);

    @Query("SELECT p FROM Page p WHERE p.site.id = :siteId AND p.path = :path")
    Page findBySiteIdAndPath(@Param("siteId") Long siteId, @Param("path") String path);

    @Query("SELECT COUNT(p) FROM Page p WHERE p.site.id = :siteId")
    long countBySiteId(@Param("siteId") Long siteId);
}