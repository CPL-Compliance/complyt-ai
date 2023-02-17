package com.complyt.utils.observability;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ContextLoggerTest {
    @Test
    void observeCtx_PrintsLogInContext_ReturnsMonoEmpty() {
        Logger logger = Logger.getLogger("Test");
        Mono<Object> actualMono = ContextLogger.observeCtx("Test String", logger::info);

        StepVerifier.create(actualMono).verifyComplete();
    }
}