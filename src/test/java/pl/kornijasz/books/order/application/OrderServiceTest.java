package pl.kornijasz.books.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.db.BookJpaRepository;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase.*;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase;
import pl.kornijasz.books.order.domain.Delivery;
import pl.kornijasz.books.order.domain.OrderStatus;
import pl.kornijasz.books.order.domain.Recipient;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookRepository;

    @Autowired
    ManipulateOrderService service;

    @Autowired
    QueryOrderUseCase queryOrderService;

    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    void userCanPlaceOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(jcip.getId(), 10))
                .build();
        // when
        PlaceOrderResponse response = service.placeOrder(command);
        // then
        assertTrue(response.isSuccess());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jcip));
    }

    @Test
    void userCanRevokeOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        // when
        // TODO fix on security module
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user(marek));
        service.updateOrderStatus(command);
        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELLED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    void userCannotRevokePaidOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, marek);
        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, user(marek));
        service.updateOrderStatus(command);
        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
        assertThrows(IllegalArgumentException.class, () -> {
            UpdateStatusCommand commandCancel = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user(marek));
            service.updateOrderStatus(commandCancel);
        });
    }

    @Test
    void userCannotRevokeShippedOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, marek);
        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, user(marek));
        service.updateOrderStatus(command);
        UpdateStatusCommand command1 = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, user(marek));
        service.updateOrderStatus(command1);
        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.SHIPPED, queryOrderService.findById(orderId).get().getStatus());
        assertThrows(IllegalArgumentException.class, () -> {
            UpdateStatusCommand commandCanel = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user(marek));
            service.updateOrderStatus(commandCanel);
        });
    }

    @Test
    void userCannotOrderNotExistingBooks() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        Long maxId = bookRepository.findAll().stream().map(book -> book.getId()).max(Long::compareTo).get();
        // then
        assertThrows(EntityNotFoundException.class, () -> {
            placedOrder(maxId + 1, 15);
        });
    }

    @Test
    void userCannotOrderNotPositiveNumberOfBooks() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        // when
        int quantity1 = 0;
        int quantity2 = -5;
        // then
        assertThrows(IllegalArgumentException.class, () -> {
            placedOrder(effectiveJava.getId(), quantity1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            placedOrder(effectiveJava.getId(), quantity2);
        });
    }

    @Test
    void userCannotRevokeOtherUsersOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String adam = "adam@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, adam);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        // when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, user("marek@example.org"));
        service.updateOrderStatus(command);
        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanRevokeOtherUsersOrder() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String marek = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, marek);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELLED, adminUser());
        service.updateOrderStatus(command);

        // then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELLED, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    public void adminCanMarkOrderAsPaid() {
        // given
        Book effectiveJava = givenEffectiveJava(50L);
        String recipient = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, recipient);
        assertEquals(35L, availableCopiesOf(effectiveJava));

        // when
        String admin = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, adminUser());
        service.updateOrderStatus(command);

        // then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
    }

    @Test
    void userCannotOrderMoreBooksThanAvailable() {
        // given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .build();
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });

        // then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));
    }

    @Test
    void shippingCostsAreAddedToTotalOrderPrice() {
        // given
        Book book = givenBook(50L ,"49.90");

        // when
        Long orderId = placedOrder(book.getId(), 1);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("59.80", order.getFinalPrice().toPlainString());
    }

    @Test
    void shippingCostsAreDiscountedOver100zlotys() {
        // given
        Book book = givenBook(50L ,"49.90");

        // when
        Long orderId = placedOrder(book.getId(), 3);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrderPrice().getItemsPrice().toPlainString());
    }

    @Test
    void cheapestBookIsHalfPriceWhenTotalOver200zlotys() {
        // given
        Book book = givenBook(50L ,"49.90");

        // when
        Long orderId = placedOrder(book.getId(), 5);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("224.55", order.getFinalPrice().toPlainString());
    }

    @Test
    void cheapestBookIsFreeWhenTotalOver400zlotys() {
        // given
        Book book = givenBook(50L ,"49.90");

        // when
        Long orderId = placedOrder(book.getId(), 10);

        // then
        RichOrder order = orderOf(orderId);
        assertEquals("449.10", order.getFinalPrice().toPlainString());
    }

    private Book givenBook(long available, String price) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal(price), available));
    }

    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }

    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }

    private User user(String email) {
        return new User(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private User adminUser() {
        return new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private Recipient recipient() {
        return recipient("marek@example.org");
    }

    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }

    private Long placedOrder(Long bookId, int copies) {
       return placedOrder(bookId, copies, "marek@example.org");
    }

    private Long placedOrder(Long bookId, int copies, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .delivery(Delivery.COURIER)
                .build();
        PlaceOrderResponse response = service.placeOrder(command);
        return response.getRight();
    }

    private RichOrder orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }

    private Long availableCopiesOf(Book book) {
        return catalogUseCase
                .findById(book.getId())
                .get()
                .getAvailable();
    }

}
