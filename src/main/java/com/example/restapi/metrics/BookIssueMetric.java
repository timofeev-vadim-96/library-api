package com.example.restapi.metrics;

import com.example.restapi.models.appEntities.IssueEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class BookIssueMetric {
    private final Counter bookIssueMetric;
    private final Counter issueRefusalMetric;

    public BookIssueMetric(MeterRegistry registry) {
        this.bookIssueMetric = registry.counter("book.issue.metric");
        this.issueRefusalMetric = registry.counter("issue.refusal.metric");
    }

    @AfterReturning(pointcut = "execution(* com.example.restapi.controllers.restControllers.IssueController.bookIssuance(..))", returning = "responseEntity")
    public void measureBookIssuance(ResponseEntity<IssueEntity> responseEntity){
        if (responseEntity.getStatusCode().equals(HttpStatus.CREATED)) bookIssueMetric.increment();
        else issueRefusalMetric.increment();
    }
}
