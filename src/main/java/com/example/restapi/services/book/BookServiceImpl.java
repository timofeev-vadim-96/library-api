package com.example.restapi.services.book;

import com.example.restapi.dao.AuthorRepository;
import com.example.restapi.dao.BookRepository;
import com.example.restapi.models.appEntities.AuthorEntity;
import com.example.restapi.models.appEntities.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository dao;
    private final AuthorRepository authorDao;

    @Override
    public BookEntity findById(long id){
        return dao.findById(id).orElse(null);
    }

    @Override
    public BookEntity save(BookEntity bookEntity){
        AuthorEntity author = bookEntity.getAuthor();
        AuthorEntity foundAuthor = authorDao
                .findFirstByFirstNameAndLastName(author.getFirstName(), author.getLastName());

        if (foundAuthor == null){
            AuthorEntity returnedAuthor = authorDao.save(author);
            bookEntity.setAuthor(returnedAuthor);
        } else {
            author.setId(foundAuthor.getId());
        }

        return dao.save(bookEntity);
    }

    @Override
    public boolean deleteById(long id){
        BookEntity book = findById(id);
        if (book == null) return false;
        else {
            dao.deleteById(id);
            return true;
        }
    }

    @Override
    public List<BookEntity> findAll() {
        return dao.findAll();
    }

    @Override
    public BookEntity findByName(String bookName) {
        return dao.findBookEntitiesByNameContaining(bookName);
    }
}
