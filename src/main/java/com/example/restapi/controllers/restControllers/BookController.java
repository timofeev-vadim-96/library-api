package com.example.restapi.controllers.restControllers;

import com.example.restapi.models.appEntities.BookEntity;
import com.example.restapi.services.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Operation(summary = "get book by id", description = "Предоставляет конкретную книгу по ее id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookEntity> getBookById(@PathVariable("id") long id){
        BookEntity bookEntity = service.findById(id);
        if (bookEntity == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(bookEntity, HttpStatus.OK);
    }

    @Operation(summary = "delete book by id", description = "Удаляет конкретную книгу по ее id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBookById(@PathVariable("id") long id){
        boolean success = service.deleteById(id);
        if (!success) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "add new book", description = "Сохранить новую книгу")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping
    public ResponseEntity<BookEntity> addBook(@RequestBody BookEntity inputBookEntity) {
        BookEntity bookEntity = service.save(inputBookEntity);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookEntity.getId())
                .toUri();
        return ResponseEntity.created(location).body(bookEntity);
    }

    @Operation(summary = "get all books", description = "Предоставляет список всех имеющихся книг в библиотеке")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<List<BookEntity>> getALl(){
        List<BookEntity> bookEntities = service.findAll();
        return new ResponseEntity<>(bookEntities, HttpStatus.OK);
    }

    @Operation(summary = "get book by title", description = "Предоставляет конкретную книгу по ее названию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    })
    @GetMapping("/byName")
    public ResponseEntity<BookEntity> findByName(@RequestParam("bookName") String bookName){
        BookEntity book = service.findByName(bookName);
        if (book == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Не удалось найти книгу с названием, содержащим имя=" + bookName);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
