package pl.kornijasz.books.order.application.price;

import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class TotalPriceDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal lowestBookPrice = lowestBookPrice(order.getItems());
        if (isGreaterOrEqual(order, 400)) {
            return lowestBookPrice;
        } else if (isGreaterOrEqual(order, 200)) {
            return lowestBookPrice.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
            return BigDecimal.ZERO;
    }

    private BigDecimal lowestBookPrice(Set<OrderItem> items) {
        return items.stream()
                .map(orderItem -> orderItem.getBook().getPrice())
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private boolean isGreaterOrEqual(Order order, int value) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(value)) >= 0;
    }
}
