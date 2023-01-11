package com.complyt.v1.exceptions;

import com.complyt.v1.exceptions.types.ComplytApiException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

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
        String message = extractMessage(httpStatus, error);

        errorMap.put("timestamp", OffsetDateTime.now());
        errorMap.put("code", httpStatus.value());
        errorMap.put("error", httpStatus.getReasonPhrase());
        errorMap.put("requestId", request.exchange().getRequest().getId());
        errorMap.put("message", message);
        errorMap.put("endpoint url", request.path());

        return errorMap;
    }

    private String extractMessage(HttpStatus httpStatus, Throwable error) {
        if (HttpStatus.valueOf(httpStatus.value()) == HttpStatus.INTERNAL_SERVER_ERROR) {
            return GENERIC_ERROR_MESSAGE;
        } else if (HttpStatus.valueOf(httpStatus.value()) == HttpStatus.BAD_REQUEST) {
            return ((ServerWebInputException) error).getMostSpecificCause().getMessage();
        } else {
            return error.getMessage();
        }
    }

    private HttpStatus getHttpStatus(Throwable error) {
        if (error instanceof ComplytApiException complytApiException) {
            return exceptionToStatusCode.getOrDefault(complytApiException.getClass(), defaultStatus);
        } else if (error instanceof ResponseStatusException responseStatusException) {
            return HttpStatus.resolve(responseStatusException.getRawStatusCode());
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}