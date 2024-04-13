package com.example.restapi.controllers.restConrollers;

import com.example.restapi.models.appEntities.IssueEntity;
import com.example.restapi.models.appEntities.ReaderEntity;
import com.example.restapi.services.issue.IssueService;
import com.example.restapi.services.reader.ReaderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReaderControllerUnitTest {
    private static ReaderController readerController;
    private static ReaderService readerServiceMock;
    private static IssueService issueServiceMock;

    @BeforeAll
    public static void setUp(){
        readerServiceMock = mock(ReaderService.class);
        issueServiceMock = mock(IssueService.class);
        readerController = new ReaderController(readerServiceMock, issueServiceMock);
    }

    @Test
    void getByIdWhenExists() {
        ReaderEntity reader = new ReaderEntity();
        reader.setId(101);
        when(readerServiceMock.findById(101)).thenReturn(reader);

        ResponseEntity<ReaderEntity> response = readerController.getById(101);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reader.getId(), response.getBody().getId());
    }

    @Test
    void getByIdWhenNotExists() {
        when(readerServiceMock.findById(102)).thenReturn(null);

        ResponseEntity<ReaderEntity> responseEntity = readerController.getById(102);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void add() {
        ReaderEntity reader = new ReaderEntity("Vladislav",
                "Ivanov",
                "+7911987453",
                "vlad.ivanov@ya.ru",
                LocalDate.of(1995, 1, 1));
        when(readerServiceMock.save(reader)).thenReturn(reader);

        ResponseEntity<ReaderEntity> response = readerController.add(reader);
        ReaderEntity returnedReader = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(returnedReader);
        assertEquals(reader.getFirstName(), returnedReader.getFirstName());
        assertEquals(reader.getSecondName(), returnedReader.getSecondName());
        assertEquals(reader.getEmail(), returnedReader.getEmail());
        assertEquals(reader.getBirthDay(), returnedReader.getBirthDay());
    }

    @Test
    void deleteWhenExists() {
        when(readerServiceMock.deleteById(103)).thenReturn(true);

        ResponseEntity<Void> response = readerController.delete(103);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteWhenNotExists() {
        when(readerServiceMock.deleteById(104)).thenReturn(false);

        ResponseEntity<Void> response = readerController.delete(104);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void readerIssuesWhenExists() {
        when(issueServiceMock.getReaderIssues(106)).thenReturn(List.of(new IssueEntity()));

        ResponseEntity<List<IssueEntity>> response = readerController.readerIssues(106);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void readerIssuesWhenNotExists() {
        when(issueServiceMock.getReaderIssues(105)).thenReturn(null);

        ResponseEntity<List<IssueEntity>> response = readerController.readerIssues(105);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getALl() {
        when(readerServiceMock.findAll()).thenReturn(List.of(new ReaderEntity()));

        ResponseEntity<List<ReaderEntity>> response = readerController.getALl();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getBySecondNameWhenExists() {
        when(readerServiceMock.findAllBySecondName("some secondName")).thenReturn(List.of(new ReaderEntity()));

        ResponseEntity<List<ReaderEntity>> response = readerController.getBySecondName("some secondName");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getBySecondNameWhenNotExists() {
        when(readerServiceMock.findAllBySecondName("some non-existent name")).thenReturn(List.of());

        ResponseEntity<List<ReaderEntity>> response = readerController.getBySecondName("some non-existent name");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getByPhoneWhenExists() {
        ReaderEntity reader = new ReaderEntity();
        reader.setPhone("+79111111111");
        when(readerServiceMock.findByPhone(reader.getPhone())).thenReturn(reader);

        ResponseEntity<ReaderEntity> response = readerController.getByPhone("+79111111111");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("+79111111111", response.getBody().getPhone());
    }

    @Test
    void getByPhoneWhenNotExists() {
        when(readerServiceMock.findByPhone("+79000000000")).thenReturn(null);

        Assertions.assertThatThrownBy(()->readerController.getByPhone("+79000000000"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getAllByBirthDayAfter() {
        when(readerServiceMock.findAllByBirthDayAfter(any(LocalDate.class))).thenReturn(null);

        ResponseEntity<List<ReaderEntity>> response = readerController.getAllByBirthDayAfter(any(LocalDate.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }}