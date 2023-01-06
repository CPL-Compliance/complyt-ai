package com.complyt.domain.customer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class CustomerAspect {

    @Around("execution(Customer *..Customer.with*(..)) && !execution(Customer *..*ComplytId(..))")
    public Customer customer_With(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("advised!");
        Customer target = (Customer) joinPoint.getTarget();
        Customer out = (Customer) joinPoint.proceed();
        return out.withComplytId(target.getComplytId());
    }
}
