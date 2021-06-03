package pl.kornijasz.books.order.domain;

import lombok.*;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.jpa.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem extends BaseEntity {

    private Long bookId;

    private int quantity;

}
