package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.models.Index;
import searchengine.models.IndexPK;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, IndexPK> {
    List<Index> findByPageId(Long pageId);
}

