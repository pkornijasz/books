package pl.kornijasz.books.order.domain;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Embeddable
public class Recipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private String street;
    private String city;
    private String zipCode;
    private String email;

    public Recipient(String name, String phone, String street, String city, String zipCode, String email) {
        this.name = name;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.email = email;
    }
}
