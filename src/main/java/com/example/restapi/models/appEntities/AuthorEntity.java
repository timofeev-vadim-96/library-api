package com.example.restapi.models.appEntities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "authors")
@Schema(name = "Автор")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Идентификатор")
    private long id;
    @Schema(name = "Имя автора")
    private String firstName;
    @Schema(name = "Фамилия автора")
    private String lastName;

    public AuthorEntity(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        AuthorEntity authorEntity = (AuthorEntity) obj;
        return authorEntity.id == this.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
