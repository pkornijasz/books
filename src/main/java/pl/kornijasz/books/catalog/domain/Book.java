package pl.kornijasz.books.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "authors")
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long coverId;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    @JsonIgnoreProperties("books")
    private Set<Author> authors;

    public Book(String title, Integer year, BigDecimal price) {
        this.title = title;
        this.year = year;
        this.price = price;
    }
}
