package pl.kornijasz.books.order.application.port;

import lombok.*;
import pl.kornijasz.books.commons.Either;
import pl.kornijasz.books.order.domain.OrderItem;
import pl.kornijasz.books.order.domain.OrderStatus;
import pl.kornijasz.books.order.domain.Recipient;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;

public interface ManipulateOrderUseCase {

    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    void updateOrderStatus(Long id, OrderStatus status);

    void deleteOrderById(Long id);

    @Value
    @Builder
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        Set<OrderItemCommand> items;
        Recipient recipient;
    }

    @Value
    static class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    class PlaceOrderResponse extends Either<String, Long> {
        public PlaceOrderResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }

        public static PlaceOrderResponse failure(String error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }
}
