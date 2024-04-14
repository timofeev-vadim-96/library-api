package com.example.restapi.controllers.restControllers;

import com.example.restapi.models.appEntities.IssueEntity;
import com.example.restapi.models.appEntities.ReaderEntity;
import com.example.restapi.services.issue.IssueService;
import com.example.restapi.services.reader.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reader")
public class ReaderController {
    private final ReaderService readerService;
    private final IssueService issueService;

    public ReaderController(ReaderService readerService, IssueService issueService) {
        this.readerService = readerService;
        this.issueService = issueService;
    }

    @Operation(summary = "get reader by id", description = "Предоставляет конкретного читателя по его id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReaderEntity> getById(@PathVariable("id") long id) {
        ReaderEntity readerEntity = readerService.findById(id);
        if (readerEntity == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(readerEntity, HttpStatus.OK);
    }

    @Operation(summary = "add new reader", description = "Добавляет нового читателя")
    @ApiResponse(responseCode = "201", description = "CREATED")
    @PostMapping
    public ResponseEntity<ReaderEntity> add(@RequestBody ReaderEntity inputReaderEntity) {
        ReaderEntity returnedReaderEntity = readerService.save(inputReaderEntity);
        return new ResponseEntity<>(returnedReaderEntity, HttpStatus.CREATED);
    }

    @Operation(summary = "delete reader by id", description = "Удаляет конкретного читателя по его id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        boolean success = readerService.deleteById(id);
        if (!success) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "get reader's issues", description = "Получить все текущие случаи взятия книг по id читателя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/{id}/issue")
    public ResponseEntity<List<IssueEntity>> readerIssues(@PathVariable("id") long id) {
        List<IssueEntity> readerIssueEntities = issueService.getReaderIssues(id);
        if (readerIssueEntities == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(readerIssueEntities, HttpStatus.OK);
    }

    @Operation(summary = "get all readers", description = "Получить список всех читателей")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<List<ReaderEntity>> getALl() {
        List<ReaderEntity> readerEntities = readerService.findAll();
        return new ResponseEntity<>(readerEntities, HttpStatus.OK);
    }

    @Operation(summary = "get readers by second name", description = "Получить всех читателей по определенной фамилии")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/bySecondName")
    public ResponseEntity<List<ReaderEntity>> getBySecondName(@RequestParam("secondName") String secondName) {
        return new ResponseEntity<>(readerService.findAllBySecondName(secondName), HttpStatus.OK);
    }

    @GetMapping("/byPhone")
    public ResponseEntity<ReaderEntity> getByPhone(@RequestParam("phone") String phone) {
        ReaderEntity reader = readerService.findByPhone(phone);
        if (reader == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователь с таким номером телефона не найден. phone=" + phone);

        return new ResponseEntity<>(readerService.findByPhone(phone), HttpStatus.OK);
    }

    @Operation(summary = "get readers born after the specified date",
            description = "Получить всех пользователей, рожденных после даты")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/older")
    public ResponseEntity<List<ReaderEntity>> getAllByBirthDayAfter(@RequestParam("date") LocalDate date) {
        return new ResponseEntity<>(readerService.findAllByBirthDayAfter(date), HttpStatus.OK);
    }
}
