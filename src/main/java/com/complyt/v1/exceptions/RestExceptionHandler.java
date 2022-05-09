package com.complyt.v1.exceptions;


import com.complyt.repositories.exceptions.OperationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.webjars.NotFoundException;

import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.notFound;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(OperationFailedException.class)
    ResponseEntity operationFailedException(OperationFailedException operationFailedException) {
        log.debug(operationFailedException.getMessage());

        return internalServerError().body(operationFailedException);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity notFoundException(NotFoundException notFoundException) {
        log.debug(notFoundException.getMessage());

        return notFound().build();
    }
}