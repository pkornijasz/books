package pl.kornijasz.books.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kornijasz.books.order.application.port.PlaceOrderUseCase;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderRepository;

import java.util.Collections;

import static java.util.Collections.*;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }
}
