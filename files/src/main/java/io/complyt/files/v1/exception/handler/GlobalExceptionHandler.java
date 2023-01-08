package io.complyt.files.v1.exception.handler;

import io.complyt.files.annotations.Generated;
import io.complyt.files.v1.exception.ComplytException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Generated
@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
    private final HttpStatus defaultStatus;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                  ApplicationContext applicationContext,
                                  Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode,
                                  HttpStatus defaultStatus,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources, applicationContext);

        this.exceptionToStatusCode = exceptionToStatusCode;
        this.defaultStatus = defaultStatus;
        this.setMessageReaders(serverCodecConfigurer.getReaders());
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> errorAttributes;
        Throwable error = getError(request);

        HttpStatus httpStatus;
        if (error instanceof ComplytException complytException) {
            httpStatus = exceptionToStatusCode.getOrDefault(complytException.getClass(), defaultStatus);
            errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorAttributes = new HashMap<>();
            errorAttributes.put("message", "Internal Error. Contact the support team");
            errorAttributes.put("endpoint url", request.path());
        }

        return ServerResponse
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorAttributes));
    }
}