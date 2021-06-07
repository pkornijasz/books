package pl.kornijasz.books.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtLessThanEqual(OrderStatus status, LocalDateTime timestamp);
}
