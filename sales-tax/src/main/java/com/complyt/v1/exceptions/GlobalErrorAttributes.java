package com.complyt.v1.exceptions;

import com.complyt.v1.config.error_messages.GenericErrorMessages;
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
        errorMap.put("url", request.path());

        return errorMap;
    }

    private String extractMessage(HttpStatus httpStatus, Throwable error) {
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            return GenericErrorMessages.INTERNAL_SERVER_ERROR;
        } else if (httpStatus == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
            return GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE;
        } else if (error instanceof ServerWebInputException serverWebInputException) {
            return serverWebInputException.getReason();
        } else {
            return error.getMessage();
        }
    }

    private HttpStatus getHttpStatus(Throwable error) {
        if (error instanceof ComplytApiException complytApiException) {
            return exceptionToStatusCode.getOrDefault(complytApiException.getClass(), defaultStatus);
        } else if (error instanceof ResponseStatusException responseStatusException) {
            return HttpStatus.resolve(responseStatusException.getStatusCode().value());
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}