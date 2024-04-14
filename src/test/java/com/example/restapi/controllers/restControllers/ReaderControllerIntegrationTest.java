package com.example.restapi.controllers.restControllers;

import com.example.restapi.controllers.dto.IssueRequest;
import com.example.restapi.models.appEntities.AuthorEntity;
import com.example.restapi.models.appEntities.BookEntity;
import com.example.restapi.models.appEntities.ReaderEntity;
import com.example.restapi.services.book.BookService;
import com.example.restapi.services.issue.IssueService;
import com.example.restapi.services.reader.ReaderService;
import com.example.restapi.testConfig.WebSecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) //для web test client
@AutoConfigureWebTestClient //для работы тестового веб-клиента
@ActiveProfiles("test") //используется тестовый application.yml
@Import(WebSecurityTestConfig.class) //отключаем security для проведения тестов
class ReaderControllerIntegrationTest {
    @Autowired
    private ReaderService readerService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private BookService bookService;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void getByIdWhenExists() {
        ReaderEntity reader = new ReaderEntity();
        reader = readerService.save(reader);

        ReaderEntity response = webTestClient.get()
                .uri("reader/" + reader.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReaderEntity.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(reader.getId(), response.getId());
    }

    @Test
    void getByIdWhenNotExists() {
        Long maxId = jdbcTemplate.queryForObject("select max(id) from readers", Long.class);
        long unexpectedId;
        if (maxId == null) unexpectedId = 1;
        else unexpectedId = maxId + 1;

        webTestClient.get()
                .uri("reader/" + unexpectedId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void add() {
        ReaderEntity reader = new ReaderEntity("Vladislav",
                "Ivanov",
                "+7911987453",
                "vlad.ivanov@ya.ru",
                LocalDate.of(1995, 1, 1));

        ReaderEntity returnedReader = webTestClient.post()
                .uri("/reader")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(reader)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ReaderEntity.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(returnedReader);
        assertTrue(returnedReader.getId() > 0);
        assertEquals(reader.getFirstName(), returnedReader.getFirstName());
        assertEquals(reader.getSecondName(), returnedReader.getSecondName());
        assertEquals(reader.getEmail(), returnedReader.getEmail());
        assertEquals(reader.getBirthDay(), returnedReader.getBirthDay());
    }

    @Test
    void deleteWhenExists() {
        Long minId = jdbcTemplate.queryForObject("select min(id) from readers", Long.class);

        if (minId == null) throw new RuntimeException("в базе данных не оказалось записей для теста...");

        webTestClient.delete()
                .uri("/reader/" + minId)
                .exchange()
                .expectStatus().isOk();
        Long unexpectedId = jdbcTemplate.queryForObject("select count(*) from readers where id = ?", Long.class, minId);

        assertTrue(unexpectedId != null && unexpectedId == 0);
    }

    @Test
    void deleteWhenNotExists() {
        Long maxId = jdbcTemplate.queryForObject("select max(id) from readers", Long.class);
        if (maxId == null) throw new RuntimeException("в базе данных не оказалось записей для теста...");
        long unexpectedId = maxId + 1;

        webTestClient.delete()
                .uri("/reader/" + unexpectedId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void readerIssuesWhenExists() {
        ReaderEntity reader = new ReaderEntity();
        BookEntity book = new BookEntity("Артас", new AuthorEntity("Кристи", "Голден"), Year.of(2009));
        readerService.save(reader);
        bookService.save(book);
        issueService.save(new IssueRequest(book.getId(), reader.getId()));

        List<ReaderEntity> response = webTestClient.get()
                .uri("reader/" + reader.getId() + "/issue")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReaderEntity>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void readerIssuesWhenNotExists() {
        Long maxId = jdbcTemplate.queryForObject("select max(id) from readers", Long.class);
        if (maxId == null) throw new RuntimeException("в базе данных не оказалось записей для теста...");
        long unexpectedId = maxId + 1;

        webTestClient.get()
                .uri("reader/" + unexpectedId + "/issue")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getALl() {
        List<ReaderEntity> response = webTestClient.get()
                .uri("reader")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReaderEntity>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void getBySecondNameWhenExists() {
        ReaderEntity reader = new ReaderEntity();
        reader.setSecondName("Tarasov");
        readerService.save(reader);

        List<ReaderEntity> response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("reader/bySecondName")
                        .queryParam("secondName", "Tarasov")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReaderEntity>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void getBySecondNameWhenNotExists() {
        List<ReaderEntity> response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("reader/bySecondName")
                        .queryParam("secondName", "R2D2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReaderEntity>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getByPhoneWhenExists() {
        ReaderEntity reader = new ReaderEntity();
        reader.setPhone("89774956834");
        readerService.save(reader);

        ReaderEntity response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("reader/byPhone")
                        .queryParam("phone", reader.getPhone())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReaderEntity.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(reader.getPhone(), response.getPhone());
    }

    @Test
    void getByPhoneWhenNotExists() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("reader/byPhone")
                        .queryParam("phone", "+700000000")
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllByBirthDayAfter() {
        ReaderEntity reader = new ReaderEntity();
        reader.setBirthDay(LocalDate.of(1991, 5, 10));
        readerService.save(reader);

        List<ReaderEntity> response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("reader/older")
                        .queryParam("date", reader.getBirthDay())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReaderEntity>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}