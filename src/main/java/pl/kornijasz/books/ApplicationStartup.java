package pl.kornijasz.books;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.db.AuthorJpaRepository;
import pl.kornijasz.books.catalog.domain.Author;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase;
import pl.kornijasz.books.order.domain.OrderItem;
import pl.kornijasz.books.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

import static pl.kornijasz.books.catalog.application.port.CatalogUseCase.*;
import static pl.kornijasz.books.order.application.port.ManipulateOrderUseCase.*;

@Component
@AllArgsConstructor
class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        initData();
        placeOrder();
    }

    private void placeOrder() {

        Book panTadeusz = catalog.findOneByTitle("Effective Java").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalog.findOneByTitle("Java Puzzlers").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Recipient recipient = Recipient
                .builder()
                .name("Piotr Kornijasz")
                .phone("123-456-789")
                .street("Kobierzyńska")
                .city("Kraków")
                .zipCode("30-382")
                .email("mail@example.com")
                .build();

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItem(panTadeusz.getId(), 16))
                .item(new OrderItem(chlopi.getId(), 7))
                .build();
        PlaceOrderResponse response = placeOrder.placeOrder(command);
        System.out.println("Created ORDER with id: " + response.getOrderId());

        queryOrder.findAll().forEach(order -> {
            System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order);
        });
    }

    private void initData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(joshua);
        authorJpaRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.00")
        );

        CreateBookCommand javaPuzzlers = new CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00")
        );

        catalog.addBook(effectiveJava);
        catalog.addBook(javaPuzzlers);

//        catalog.addBook(new CreateBookCommand("Harry Potter i Komnata Tajemnic", "JK Rowling", 1998, new BigDecimal("29.90")));
//        catalog.addBook(new CreateBookCommand("Władca Pierścieni: Dwie Wieże", "JRR Tolkien", 1954, new BigDecimal("29.90")));
//        catalog.addBook(new CreateBookCommand("Mężczyźni, którzy nienawidzą kobiet", "Stieg Larsson", 2005, new BigDecimal("29.90")));
//        catalog.addBook(new CreateBookCommand("Sezon Burz", "Andrzej Sapkowski", 2013, new BigDecimal("29.90")));
//        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, new BigDecimal("19.90")));
//        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884, new BigDecimal("29.90")));
//        catalog.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1904, new BigDecimal("11.90")));
//        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1899, new BigDecimal("14.90")));
    }

}
