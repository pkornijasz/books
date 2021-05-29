package pl.kornijasz.books.order.infrastructure;

import org.springframework.stereotype.Repository;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderRepository;
import pl.kornijasz.books.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();

    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0L);

    @Override
    public Order save(Order order) {
        if (order.getId() != null) {
            storage.put(order.getId(), order);
        } else {
            Long nextId = nextId();
            order.setId(nextId);
            order.setCreatedAt(LocalDateTime.now());
            storage.put(nextId, order);
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = storage.get(id);
        order.setStatus(status);
        storage.put(id, order);
    }

    private Long nextId() {
        return ID_NEXT_VALUE.incrementAndGet();
    }
}
