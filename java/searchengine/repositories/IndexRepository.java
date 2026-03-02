package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.models.Index;
import searchengine.models.IndexPK;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, IndexPK> {

    List<Index> findByPageId(Long pageId);

    @Query("SELECT i FROM Index i JOIN FETCH i.lemma WHERE i.page.id IN :pageIds")
    List<Index> findByPageIds(@Param("pageIds") List<Long> pageIds);

    @Query("SELECT COUNT(i) FROM Index i WHERE i.lemma.id = :lemmaId")
    long countByLemmaId(@Param("lemmaId") Long lemmaId);
}