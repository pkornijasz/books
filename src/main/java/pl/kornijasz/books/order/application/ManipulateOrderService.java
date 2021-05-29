package pl.kornijasz.books.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderRepository;
import pl.kornijasz.books.order.domain.OrderStatus;

@Service
@RequiredArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
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

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        repository.updateOrderStatus(id, status);
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }
}
