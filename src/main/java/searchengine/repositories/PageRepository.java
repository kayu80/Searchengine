package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.models.Page;
import java.util.List;
import java.util.Set;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByLemmas(Set<String> lemmaSet);
}
