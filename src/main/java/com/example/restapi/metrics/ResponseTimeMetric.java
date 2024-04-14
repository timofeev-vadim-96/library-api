package com.example.restapi.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class ResponseTimeMetric {
    private final Timer timer;

    public ResponseTimeMetric(MeterRegistry registry) {
        this.timer = registry.timer("library.api.response.time.metric");
    }

    @Around("execution(* com.example.restapi.controllers..*.*(..))")
    public Object measureResponseTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        timer.record(executionTime, TimeUnit.MILLISECONDS);
        return proceed;
    }
}
