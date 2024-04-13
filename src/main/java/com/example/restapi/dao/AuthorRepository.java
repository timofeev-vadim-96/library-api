package com.example.restapi.dao;

import com.example.restapi.models.appEntities.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    AuthorEntity findFirstByFirstNameAndLastName(String firstName, String lastName);
}
