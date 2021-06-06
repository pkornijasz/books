package pl.kornijasz.books.order.application.port;

import lombok.Value;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderItem;
import pl.kornijasz.books.order.domain.OrderStatus;
import pl.kornijasz.books.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QueryOrderUseCase {

    List<RichOrder> findAll();

    Optional<Order> findById(Long id);

    @Value
    class RichOrder {
        Long id;
        OrderStatus status;
        Set<OrderItem> items;
        Recipient recipient;
        LocalDateTime createdAt;

        public BigDecimal totalPrice() {
            return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
