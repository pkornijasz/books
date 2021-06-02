package pl.kornijasz.books.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase.RichOrder;
import pl.kornijasz.books.order.domain.Order;
import pl.kornijasz.books.order.domain.Recipient;

import java.util.List;

import static pl.kornijasz.books.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrdersController {

    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;

    @GetMapping
    public List<RichOrder> getOrders() {
        return queryOrder.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return queryOrder
                .findById(id)
                .map((ResponseEntity::ok))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void placeOrder(@RequestBody PlaceOrderCommand command) {
        manipulateOrder.placeOrder(command);
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody Order order) {
        manipulateOrder.updateOrderStatus(id, order.getStatus());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }

    @Data
    static class RecipientCommand {
        String name;
        String phone;
        String street;
        String city;
        String zipCode;
        String email;

        Recipient toRecipient() {
            return new Recipient(name, phone, street, city, zipCode, email);
        }
    }
}
