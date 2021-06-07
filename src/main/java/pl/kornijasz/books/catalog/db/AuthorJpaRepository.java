package pl.kornijasz.books.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kornijasz.books.catalog.domain.Author;

import java.util.Optional;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);
}
