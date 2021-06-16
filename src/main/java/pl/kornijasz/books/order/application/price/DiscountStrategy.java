package pl.kornijasz.books.order.application.price;

import pl.kornijasz.books.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
