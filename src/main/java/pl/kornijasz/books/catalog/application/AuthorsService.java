package pl.kornijasz.books.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kornijasz.books.catalog.application.port.AuthorsUseCase;
import pl.kornijasz.books.catalog.db.AuthorJpaRepository;
import pl.kornijasz.books.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorsService implements AuthorsUseCase {

    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
