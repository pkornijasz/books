package pl.kornijasz.books.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum Delivery {
    SELF_PICUP(BigDecimal.ZERO),
    COURIER(new BigDecimal("9.90"));

    private final BigDecimal price;
}
