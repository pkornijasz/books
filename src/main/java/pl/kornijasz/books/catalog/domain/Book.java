package pl.kornijasz.books.catalog.domain;

import lombok.*;

@Getter
@RequiredArgsConstructor
@ToString
public class Book {
    private final Long id;
    private final String title;
    private final String author;
    private final Integer year;
}
