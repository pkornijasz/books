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
import pl.kornijasz.books.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.kornijasz.books.catalog.domain.Book;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static pl.kornijasz.books.catalog.application.port.CatalogUseCase.*;
import static pl.kornijasz.books.catalog.application.port.CatalogUseCase.CreateBookCommand;

@RestController
@RequestMapping("/catalog")
@AllArgsConstructor
public class CatalogController {

    private final CatalogUseCase catalog;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author) {
        if (title.isPresent() && author.isPresent()) {
            return catalog.findByTitleAndAuthor(title.get(), author.get());
        } else if (title.isPresent()) {
            return catalog.findByTitle(title.get());
        } else if (author.isPresent()) {
            return catalog.findByAuthor(author.get());
        }
        return catalog.findAll();
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
