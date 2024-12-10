package io.complyt.utils.exceptions.types;

import org.springframework.web.server.ServerWebInputException;

public class ComplytException extends ServerWebInputException {

    public ComplytException(String reason) {
        super(reason);
    }
}
