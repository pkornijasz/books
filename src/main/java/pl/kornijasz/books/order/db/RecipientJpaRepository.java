package pl.kornijasz.books.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kornijasz.books.order.domain.Recipient;

import java.util.Optional;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByEmailIgnoreCase(String email);
}
