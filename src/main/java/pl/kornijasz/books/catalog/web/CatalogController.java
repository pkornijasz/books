package pl.kornijasz.books.catalog.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.domain.Author;
import pl.kornijasz.books.catalog.domain.Book;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.kornijasz.books.catalog.application.port.CatalogUseCase.*;

@RestController
@RequestMapping("/catalog")
@AllArgsConstructor
//@CrossOrigin(origins = "https://books-front-pk.herokuapp.com/", allowedHeaders = "*")
public class CatalogController {

    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RestBook> getAll(
            HttpServletRequest request,
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {
        List<Book> books;
        if (title.isPresent() && author.isPresent()) {
            books = catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            books = catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            books = catalog.findByAuthor(author.get());
        } else {
            books = catalog.findAll();
        }
        return books.stream()
                .map(book -> toRestBook(book, request))
                .collect(Collectors.toList());
    }

    private RestBook toRestBook(Book book, HttpServletRequest request) {
        String coverUrl = Optional
                .ofNullable(book.getCoverId())
                .map(coverId -> ServletUriComponentsBuilder
                    .fromContextPath(request)
                    .path("/uploads/{id}/file")
                    .build(coverId)
                    .toASCIIString())
                .orElse(null);
        return new RestBook(
                book.getId(),
                book.getTitle(),
                book.getYear(),
                book.getPrice(),
                coverUrl,
                book.getAvailable(),
                toRestAuthors(book.getAuthors())
        );
    }

    private Set<RestAuthor> toRestAuthors(Set<Author> authors) {
        return authors.stream()
                .map(x -> new RestAuthor(x.getName()))
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
//        if (id.equals(44L)) {
//            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
//        }
        return catalog
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateBook(@PathVariable Long id, @RequestBody RestBookCommand command) {
        UpdateBookResponse response = catalog.updateBook(command.toUpdateCommand(id));
        if (!response.isSuccess()) {
            String message = String.join(", ", response.getErrors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addBookCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
//        System.out.println("Got file: " + file.getOriginalFilename());
        catalog.updateBookCover(new UpdateBookCoverCommand(id, file.getBytes(), file.getContentType(), file.getOriginalFilename()));
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        catalog.removeById(id);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookCover(@PathVariable Long id) {
        catalog.removeBookCover(id);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addBook(@Valid @RequestBody RestBookCommand command) {
        Book book = catalog.addBook(command.toCreateCommand());
        return ResponseEntity.created(createBookUri(book)).build();
    }

    private URI createBookUri(Book book) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + book.getId().toString()).build().toUri();
    }

    @Data
    private static class RestBookCommand {

        @NotBlank(message = "Please provide a title")
        private String title;

        @NotEmpty
        private Set<Long> authors;

        @NotNull
        private Integer year;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;

        @NotNull
        @PositiveOrZero
        private Long available;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(title, authors, year, price, available);
        }

        UpdateBookCommand toUpdateCommand(Long id) {
            return new UpdateBookCommand(id, title, authors, year, price);
        }
    }
}
