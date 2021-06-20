package pl.kornijasz.books.catalog.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kornijasz.books.catalog.application.port.CatalogInitializerUseCase;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.db.AuthorJpaRepository;
import pl.kornijasz.books.catalog.domain.Author;
import pl.kornijasz.books.catalog.domain.Book;
import pl.kornijasz.books.order.application.port.ManipulateOrderUseCase;
import pl.kornijasz.books.order.application.port.QueryOrderUseCase;
import pl.kornijasz.books.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

import static pl.kornijasz.books.order.application.port.ManipulateOrderUseCase.*;

@Slf4j
@RestController
@Secured({"ROLE_ADMIN"})
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogInitializerUseCase initializer;

//    @PreAuthorize()
    @PostMapping("/initialization")
    @Transactional
    public void initialize() {
        initializer.initialize();
    }
}
