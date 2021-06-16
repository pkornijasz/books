package pl.kornijasz.books.order.application;

import lombok.Value;
import pl.kornijasz.books.order.application.price.OrderPrice;
import pl.kornijasz.books.order.domain.OrderItem;
import pl.kornijasz.books.order.domain.OrderStatus;
import pl.kornijasz.books.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public class RichOrder {
    Long id;
    OrderStatus status;
    Set<OrderItem> items;
    Recipient recipient;
    LocalDateTime createdAt;
    OrderPrice orderPrice;
    BigDecimal finalPrice;
}
