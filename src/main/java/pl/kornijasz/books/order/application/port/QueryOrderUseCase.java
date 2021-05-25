package pl.kornijasz.books.order.application.port;

import pl.kornijasz.books.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
