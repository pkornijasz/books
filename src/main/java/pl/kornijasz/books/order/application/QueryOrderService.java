package pl.kornijasz.books.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase;
import pl.kornijasz.books.order.application.price.OrderPrice;
import pl.kornijasz.books.order.application.price.PriceService;
import pl.kornijasz.books.order.db.OrderJpaRepository;
import pl.kornijasz.books.order.domain.Order;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository repository;
//    private final BookJpaRepository catalogRepository;
    private final PriceService priceService;

    @Override
    @Transactional
    public List<RichOrder> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toRichOrder)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<RichOrder> findById(Long id) {
        return repository.findById(id).map(this::toRichOrder);
    }

    private RichOrder toRichOrder(Order order) {
        OrderPrice orderPrice = priceService.calculatePrice(order);
        return new RichOrder(
                order.getId(),
                order.getStatus(),
                order.getItems(),
                order.getRecipient(),
                order.getCreatedAt(),
                orderPrice,
                orderPrice.finalPrice()
        );
    }
}
