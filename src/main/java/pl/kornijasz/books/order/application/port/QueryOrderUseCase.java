package pl.kornijasz.books.order.application.port;

import pl.kornijasz.books.order.application.RichOrder;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {

    List<RichOrder> findAll();

    Optional<RichOrder> findById(Long id);

}
