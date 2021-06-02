package pl.kornijasz.books.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kornijasz.books.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
