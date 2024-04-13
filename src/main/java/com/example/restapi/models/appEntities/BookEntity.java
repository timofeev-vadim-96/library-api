package com.example.restapi.models.appEntities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Entity
@Data
@NoArgsConstructor
@Table(name = "books")
@Schema(name = "Книга")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Идентификатор")
    private long id;
    @Schema(name = "Название книги")
    private String name;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private AuthorEntity author;
    @Column(name = "written_at")
    private Year writtenAt;

    public BookEntity(String name, AuthorEntity author, Year writtenAt) {
        this.name = name;
        this.author = author;
        this.writtenAt = writtenAt;
    }

    @Override
    public boolean equals(Object obj) {
        BookEntity book = (BookEntity) obj;
        return book.id == this.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}

