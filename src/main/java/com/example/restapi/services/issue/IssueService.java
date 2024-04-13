package com.example.restapi.services.issue;

import com.example.restapi.controllers.dto.IssueRequest;
import com.example.restapi.models.appEntities.BookEntity;
import com.example.restapi.models.appEntities.IssueEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueService {
    IssueEntity findById(long id);

    IssueEntity save(IssueRequest issueRequest);

    void deleteById(long id);

    List<IssueEntity> findAll();


    List<IssueEntity> getReaderIssues(long readerId);

    IssueEntity closeIssue(long issueId);
    public List<IssueEntity> findAllByIssueAtBetween(LocalDateTime inputFrom, LocalDateTime inputTo);
}
