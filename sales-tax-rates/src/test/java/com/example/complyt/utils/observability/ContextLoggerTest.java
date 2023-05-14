package com.example.complyt.utils.observability;

import com.complyt.utils.observability.ContextLogger;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.logging.Logger;

class ContextLoggerTest {
    @Test
    void observeCtx_PrintsLogInContext_ReturnsMonoEmpty() {
        Logger logger = Logger.getLogger("Test");
        ContextLogger contextLogger = new ContextLogger();
        Mono<Object> actualMono = contextLogger.observeCtx("Test String", logger::info);

        StepVerifier.create(actualMono).verifyComplete();
    }
}