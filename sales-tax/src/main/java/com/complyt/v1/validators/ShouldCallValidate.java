package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;


@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShouldCallValidate {

    Map<HttpMethod, String> methodsMapValidate;

    public boolean apply(ServerRequest serverRequest) {
        HttpMethod method = serverRequest.method();
        String path = serverRequest.path();

        return methodsMapValidate.containsKey(method) &&
                path.matches(methodsMapValidate.get(method));
    }

}
