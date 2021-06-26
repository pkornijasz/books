package pl.kornijasz.books.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.db.AuthorJpaRepository;
import pl.kornijasz.books.catalog.db.BookJpaRepository;
import pl.kornijasz.books.catalog.domain.Author;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.uploads.application.port.UploadUseCase;
import pl.kornijasz.books.uploads.domain.Upload;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.kornijasz.books.uploads.application.port.UploadUseCase.SaveUploadCommand;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {

    private final BookJpaRepository repository;
    private final AuthorJpaRepository authorRepository;
    private final UploadUseCase upload;

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return repository.findByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public List<Book> findByAuthor(String author) {
//        return repository.findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(author, author);
        return repository.findByAuthor(author);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return repository.findByTitleAndAuthor(title ,author);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return repository.findDistinctFirstByTitleStartsWithIgnoreCase(title);
    }

    @Override
    @Transactional
    public Book addBook(CreateBookCommand command) {
        Book book = toBook(command);
        return repository.save(book);
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.getTitle(), command.getYear(), command.getPrice(), command.getAvailable());
        Set<Author> authors = fetchAuthorsByIds(command.getAuthors());
        updateBooks(book, authors);
        return book;
    }

    private void updateBooks(Book book, Set<Author> authors) {
        book.removeAuthors();
        authors.forEach(book::addAuthor);
    }

    private Set<Author> fetchAuthorsByIds(Set<Long> authors) {
        return authors
                .stream()
                .map(authorId -> authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Unable to find author with id: " + authorId))
                ).collect(Collectors.toSet());
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository.findById(command.getId())
                .map(book -> {
                    Book updatedBook = updateFields(command, book);
//                    repository.save(updatedBook); -> wystarczy @Transactional
                    return UpdateBookResponse.SUCCESS;
                }).orElseGet(() -> new UpdateBookResponse(false, Arrays.asList("Book not found with id: " + command.getId())));
    }

    private Book updateFields(UpdateBookCommand command, Book book) {

        if (command.getTitle() != null) {
            book.setTitle(command.getTitle());
        }

        if (command.getAuthors() != null && !command.getAuthors().isEmpty()) {
            updateBooks(book, fetchAuthorsByIds(command.getAuthors()));
        }

        if (command.getYear() != null) {
            book.setYear(command.getYear());
        }
        if (command.getPrice() != null) {
            book.setPrice(command.getPrice());
        }
        return book;
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
//        int length = command.getFile().length;
//        System.out.println("Received cover command: " + command.getFilename() + " bytes: " + length);
        repository.findById(command.getId())
                .ifPresent(book -> {
                    Upload savedUpload = upload.save(new SaveUploadCommand(command.getFilename(), command.getFile(), command.getContentType()));
                    book.setCoverId(savedUpload.getId());
                    repository.save(book);
                });
    }

    @Override
    public void removeBookCover(Long id) {
        repository.findById(id)
                .ifPresent(book -> {
                    if (book.getCoverId() != null) {
                        upload.removeById(book.getCoverId());
                        book.setCoverId(null);
                        repository.save(book);
                    }
                });
    }
}
