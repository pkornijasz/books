package pl.kornijasz.books.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

import static pl.kornijasz.books.order.application.port.ManipulateOrderUseCase.*;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase placeOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorJpaRepository;

    @PostMapping("/data")
    @Transactional
    public void initialize() {
        initData();
        placeOrder();
    }

    private void placeOrder() {
        Book effectiveJava = catalog.findOneByTitle("Effective Java")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book puzzlers = catalog.findOneByTitle("Java Puzzlers")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        // create recipient
        Recipient recipient = Recipient
                .builder()
                .name("Jan Kowalski")
                .phone("123-456-789")
                .street("Armii Krajowej 31")
                .city("Krakow")
                .zipCode("30-150")
                .email("jan@example.org")
                .build();

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItemCommand(effectiveJava.getId(), 16))
                .item(new OrderItemCommand(puzzlers.getId(), 7))
                .build();

        PlaceOrderResponse response = placeOrder.placeOrder(command);
        String result = response.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        System.out.println(result);

        // list all orders
        queryOrder.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));
    }

    private void initData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(joshua);
        authorJpaRepository.save(neal);

        CatalogUseCase.CreateBookCommand effectiveJava = new CatalogUseCase.CreateBookCommand(
                "Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.00"),
                50L
        );
        CatalogUseCase.CreateBookCommand javaPuzzlers = new CatalogUseCase.CreateBookCommand(
                "Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.00"),
                50L
        );
        catalog.addBook(javaPuzzlers);
        catalog.addBook(effectiveJava);
    }
}
