package pl.kornijasz.books;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.order.application.port.PlaceOrderUseCase;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase;
import pl.kornijasz.books.order.domain.OrderItem;
import pl.kornijasz.books.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

import static pl.kornijasz.books.catalog.application.port.CatalogUseCase.*;
import static pl.kornijasz.books.order.application.port.PlaceOrderUseCase.*;

@Component
class ApplicationStartup implements CommandLineRunner {

    private final CatalogUseCase catalog;
    private final PlaceOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final String title;
    private final String author;
    private final Long limit;

    public ApplicationStartup(CatalogUseCase catalog, PlaceOrderUseCase placeOrder, QueryOrderUseCase queryOrder, @Value("${books.catalog.title}") String title, @Value("${books.catalog.author}") String author, @Value("${books.catalog.limit:3}") Long limit) {
        this.catalog = catalog;
        this.placeOrder = placeOrder;
        this.queryOrder = queryOrder;
        this.title = title;
        this.author = author;
        this.limit = limit;
    }

    @Override
    public void run(String... args) throws Exception {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {

        Book panTadeusz = catalog.findOneByTitle("Pan Tadeusz").orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalog.findOneByTitle("Chłopi").orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Recipient recipient = Recipient
                .builder()
                .name("Piotr")
                .phone("123-456-789")
                .street("Szeroka")
                .city("kraków")
                .zipCode("12-345")
                .email("mail@example.com")
                .build();

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItem(panTadeusz, 16))
                .item(new OrderItem(chlopi, 7))
                .build();
        PlaceOrderResponse response = placeOrder.placeOrder(command);
        System.out.println("Created ORDER with id: " + response.getOrderId());

        queryOrder.findAll().forEach(order -> {
            System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order);
        });
    }

    private void searchCatalog() {
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CreateBookCommand("Harry Potter i Komnata Tajemnic", "JK Rowling", 1998, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Władca Pierścieni: Dwie Wieże", "JRR Tolkien", 1954, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Mężczyżni, którzy nienawidzą kobiet", "Stieg Larsson", 2005, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Sezon Burz", "Andrzej Sapkowski", 2013, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, new BigDecimal("19.90")));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Chłopi", "Władysław Reymont", 1904, new BigDecimal("11.90")));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1899, new BigDecimal("14.90")));
    }

    private void findByTitle() {
        List<Book> books = catalog.findByTitle(title);
        books.stream().limit(limit).forEach(System.out::println);
    }

    private void findAndUpdate() {
        System.out.println("Updating book...");
        catalog.findOneByTitleAndAuthor("Pan Tadeusz", "Mickiewicz")
                .ifPresent(book -> {
                    UpdateBookCommand command = UpdateBookCommand.builder()
                            .id(book.getId())
                            .title("Pan Tadeusz, czyli ostatni zajazd na Litwie")
                            .author(book.getAuthor())
                            .year(book.getYear())
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result: " + response.isSuccess());
                });
    }
}
