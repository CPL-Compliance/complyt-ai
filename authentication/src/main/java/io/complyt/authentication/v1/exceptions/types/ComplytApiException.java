package io.complyt.authentication.v1.exceptions.types;

import io.complyt.authentication.annotations.Generated;
import org.springframework.web.server.ServerWebInputException;

@Generated
public class ComplytApiException extends ServerWebInputException {
    public ComplytApiException(String message) {
        super(message);
    }
}