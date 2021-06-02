package pl.kornijasz.books.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kornijasz.books.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
}
