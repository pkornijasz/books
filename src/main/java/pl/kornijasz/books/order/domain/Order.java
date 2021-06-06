package pl.kornijasz.books.order.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.kornijasz.books.jpa.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipient_id")
    private Set<OrderItem> items;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })// @ManyToOne EAGER is default!
//    @JoinColumn(name = "order_id")
    private Recipient recipient;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateStatus(OrderStatus newStatus) {
        this.status = status.updateStatus(newStatus);
    }
}
