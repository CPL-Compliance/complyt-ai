package com.complyt.exceptions;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @NonNull
    private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;

    @NonNull
    private final HttpStatus defaultStatus;

    private static final String GENERIC_ERROR_MESSAGE = "The request failed due to an internal error. Please contact support@complyt.io if this continues";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        Map<String, Object> errorMap = new HashMap<>();

        HttpStatus httpStatus = getHttpStatus(error);

        errorMap.put("timestamp", OffsetDateTime.now());
        errorMap.put("code", httpStatus.value());
        errorMap.put("error", httpStatus.getReasonPhrase());
        errorMap.put("requestId", request.exchange().getRequest().getId());
        errorMap.put("message", HttpStatus.valueOf(httpStatus.value()) == HttpStatus.INTERNAL_SERVER_ERROR ? GENERIC_ERROR_MESSAGE : error.getMessage());
        errorMap.put("endpoint url", request.path());

        return errorMap;
    }

    private HttpStatus getHttpStatus(Throwable error) {
        if (error instanceof ComplytException complytException) {
            return exceptionToStatusCode.getOrDefault(complytException.getClass(), defaultStatus);
        } else if (error instanceof ResponseStatusException responseStatusException) {
            return HttpStatus.resolve(responseStatusException.getRawStatusCode());
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}