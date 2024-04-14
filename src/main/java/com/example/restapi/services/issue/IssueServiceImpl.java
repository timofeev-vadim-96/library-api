package com.example.restapi.services.issue;

import com.example.restapi.controllers.dto.IssueRequest;
import com.example.restapi.dao.IssueRepository;
import com.example.restapi.exceptions.DebtorException;
import com.example.restapi.exceptions.TheBookIsBusy;
import com.example.restapi.models.appEntities.BookEntity;
import com.example.restapi.models.appEntities.IssueEntity;
import com.example.restapi.models.appEntities.ReaderEntity;
import com.example.restapi.services.book.BookService;
import com.example.restapi.services.reader.ReaderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {
    private final IssueRepository issueDao;
    private final ReaderService readerService;
    private final BookService bookService;
    private final Environment environment;

    @Override
    public IssueEntity findById(long id){
        return issueDao.findById(id).orElse(null);
    }

    @Override
    public IssueEntity save(IssueRequest issueRequest) {
        ReaderEntity reader = readerService.findById(issueRequest.getReaderId());
        BookEntity book = bookService.findById(issueRequest.getBookId());

        if (reader == null) {
            log.info("Запрос на добавление факта выдачи ссылается на не существующего читателя. readerId={}",
                    issueRequest.getReaderId());
            return null;
        }

        else if (book == null) {
            log.info("Запрос на добавление факта выдачи ссылается на не существующую книгу. readerId={}",
                    issueRequest.getBookId());
            return null;
        }

        else if (isTheReaderInDebt(reader)){
            throw new DebtorException(String.format("Читатель с id=%d является должником.", reader.getId()));
        }

        else if (isBookBusy(book)){
            throw new TheBookIsBusy(String.format("Книга с id=%d находится на руках у другого читателя.", book.getId()));
        }

        IssueEntity issue = new IssueEntity(book, reader);
        issue.setIssueAt();

        return issueDao.save(issue);
    }

    @Override
    public void deleteById(long id) {
        issueDao.deleteById(id);
    }

    @Override
    public List<IssueEntity> findAll() {
        return issueDao.findAll();
    }

    private boolean isTheReaderInDebt(ReaderEntity readerEntity){
        long quantityBooksOnHand = issueDao.findAll().stream()
                .filter(issue -> issue.getReader().equals(readerEntity) && issue.getReturnedAt() == null)
                .count();

        Integer maxAllowedBooks = environment.getProperty("${application.issue.max-allowed-books:1}", Integer.class);
        if (maxAllowedBooks == null) maxAllowedBooks = 1;
        return quantityBooksOnHand > maxAllowedBooks;
    }

    @Override
    public List<IssueEntity> getReaderIssues(long readerId) {
        ReaderEntity readerEntity = readerService.findById(readerId);
        if (readerEntity == null) {
            log.info("Запрос на получения списка выдач книг на руки ссылается на не существующего читателя. readerId={}",
                    readerId);
            return null;
        }
        return issueDao.findAll().stream()
                .filter(issue -> issue.getReader().equals(readerEntity) && issue.getReturnedAt() == null)
                .collect(Collectors.toList());
    }

    @Override
    public IssueEntity closeIssue(long issueId) {
        IssueEntity issueEntity = issueDao.findById(issueId).orElse(null);
        if (issueEntity == null){
            log.info("Запрос на закрытие факта выдачи ссылается на несуществующий факт выдачи. issueId={}", issueId);
            return null;
        }
        issueEntity.setReturnedAt();
        issueDao.save(issueEntity);
        return issueEntity;
    }

    private boolean isBookBusy(BookEntity bookEntity){
        IssueEntity issueEntity = issueDao.findAll().stream()
                .filter(iss -> iss.getBook().equals(bookEntity) && iss.getReturnedAt() == null)
                .findFirst()
                .orElse(null);
        return issueEntity != null;
    }

    @Override
    public List<IssueEntity> findAllByIssueAtBetween(LocalDateTime from, LocalDateTime to){
        return issueDao.findAllByIssueAtBetween(from, to);
    }
}
