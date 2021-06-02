package pl.kornijasz.books.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase;
import pl.kornijasz.books.order.db.OrderJpaRepository;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderStatus;

@Service
@RequiredArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {

    private final OrderJpaRepository repository;

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
        repository.findById(id)
                .ifPresent(order -> {
                    order.setStatus(status);
                    repository.save(order);
                });
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }
}
