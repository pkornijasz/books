package pl.kornijasz.books.order.domain;

import lombok.*;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.jpa.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    private int quantity;
}
