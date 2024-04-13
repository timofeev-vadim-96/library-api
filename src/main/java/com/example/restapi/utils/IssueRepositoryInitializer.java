package com.example.restapi.utils;

import com.example.restapi.controllers.dto.IssueRequest;
import com.example.restapi.services.issue.IssueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class IssueRepositoryInitializer {
    private final IssueService issueService;

    @EventListener(ContextRefreshedEvent.class)
    public void init(){
        if (issueService.findAll().isEmpty()) {
            issueService.save(new IssueRequest(1L, 1L));
            issueService.save(new IssueRequest(2L, 1L));
            issueService.save(new IssueRequest(3L, 2L));
        }

        log.info("Инициализация тестовых значений кейсов по взятию книг произведена");
    }
}
