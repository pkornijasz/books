package pl.kornijasz.books.order.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase;
import pl.kornijasz.books.order.db.OrderJpaRepository;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.OrderStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static pl.kornijasz.books.order.application.port.ManipulateOrderUseCase.*;

@Slf4j
@Component
@AllArgsConstructor
public class AbandonedOrdersJob {

    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;

    @Transactional
    @Scheduled(cron = "${app.orders.abandon-cron}")
    public void run() {
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime olderThan = LocalDateTime.now().minus(paymentPeriod); // .minusMinutes(5);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            // TODO: naprawić w module security
            String adminEmail = "admin@example.org";
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, adminEmail);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
