package pl.kornijasz.books.catalog.application.port;

import pl.kornijasz.books.catalog.domain.Author;

import java.util.List;

public interface AuthorsUseCase {

    List<Author> findAll();
}
