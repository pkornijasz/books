package pl.kornijasz.books.order.domain;

import lombok.Value;
import pl.kornijasz.books.catalog.domain.Book;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
