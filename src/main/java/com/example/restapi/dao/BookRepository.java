package com.example.restapi.dao;

import com.example.restapi.models.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    BookEntity findBookEntitiesByNameContaining(String bookName);
}
