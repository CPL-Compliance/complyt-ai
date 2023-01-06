package com.complyt.domain;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class TransactionAspect {

    @Around("execution(Transaction *..Transaction.with*(..)) && !execution(Transaction *..*ComplytId(..))")
    public Transaction transaction_With(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("advised!");
        Transaction target = (Transaction) joinPoint.getTarget();
        Transaction out = (Transaction) joinPoint.proceed();
        return out.withComplytId(target.getComplytId());
    }
}
