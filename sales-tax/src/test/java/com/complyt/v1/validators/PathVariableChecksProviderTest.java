package com.complyt.v1.validators;

import com.complyt.v1.models.checkables.SourceCheckable;
import com.complyt.v1.validators.param_checker.ComplytIdPathVariableCheckable;
import com.complyt.v1.validators.param_checker.PathVariableChecksProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PathVariableChecksProviderTest {
    PathVariableChecksProvider pathVariableChecksProvider;

    @BeforeEach
    void setup() {
        Map<String, Function<ServerRequest, Mono<String>>> variableChecksMap = new HashMap<>();
        variableChecksMap.put("complytId", ComplytIdPathVariableCheckable.COMPLYT_ID_CHECK);
//        pathVariableChecksProvider = new PathVariableChecksProvider<>(pathVariableChecksProvider);
    }
}
