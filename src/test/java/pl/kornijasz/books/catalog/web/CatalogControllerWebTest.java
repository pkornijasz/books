package pl.kornijasz.books.catalog.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kornijasz.books.catalog.application.port.CatalogUseCase;
import pl.kornijasz.books.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
class CatalogControllerWebTest {

    @MockBean
    CatalogUseCase catalogUseCase;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldGetAllBooks() throws Exception {
        // given
        Book effective = new Book("Effective Java", 2005, new BigDecimal("99.90"), 50L);
        Book concurrency = new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), 50L);
        Mockito.when(catalogUseCase.findAll()).thenReturn(List.of(effective, concurrency));
        // expect
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
