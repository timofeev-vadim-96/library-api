package com.example.restapi.models.appEntities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "readers")
public class ReaderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    private String phone;
    private String email;
    @Column(name = "birth_day")
    private LocalDate birthDay;

    public ReaderEntity(String firstName, String secondName, String phone, String email, LocalDate birthDay) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.phone = phone;
        this.email = email;
        this.birthDay = birthDay;
    }

    @Override
    public boolean equals(Object obj) {
        ReaderEntity reader = (ReaderEntity) obj;
        return reader.id == this.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
