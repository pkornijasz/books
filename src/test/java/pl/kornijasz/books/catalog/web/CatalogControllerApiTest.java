package pl.kornijasz.books.catalog.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.domain.Book;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CatalogControllerApiTest {

//    @LocalServerPort
//    private int port;
//
//    @MockBean
//    CatalogUseCase catalogUseCase;
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    @Test
//    void getAllBooks() {
//        // given
//        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.90"), 50L);
//        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), 50L);
//        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));
//        ParameterizedTypeReference<List<Book>> type = new ParameterizedTypeReference<>() {
//        };
//        // when
//        String url = "http://localhost:" + port + "/catalog";
//        RequestEntity<Void> request = RequestEntity.get(URI.create("http://localhost:" + port + "/catalog")).build();
//        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
//        // then
//        assertEquals(2, response.getBody().size());
//    }
}
