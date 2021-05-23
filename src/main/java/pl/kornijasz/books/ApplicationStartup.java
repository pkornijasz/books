package pl.kornijasz.books;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.kornijasz.books.catalog.application.CatalogController;
import pl.kornijasz.books.catalog.domain.Book;

import java.util.List;

@Component
@RequiredArgsConstructor
class ApplicationStartup implements CommandLineRunner {

    private final CatalogController catalogController;

    @Override
    public void run(String... args) throws Exception {
        List<Book> books = catalogController.findByTitle("Pan");
        books.forEach(System.out::println);
    }
}
