package pl.kornijasz.books.order.domain;

import lombok.*;
import pl.kornijasz.books.jpa.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Embeddable
public class Recipient extends BaseEntity {

    private String name;

    private String phone;

    private String street;

    private String city;

    private String zipCode;

    private String email;

}
