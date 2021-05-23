package pl.kornijasz.books.catalog.domain;

import java.util.List;

public interface CatalogRepository {
    List<Book> findAll();
}
