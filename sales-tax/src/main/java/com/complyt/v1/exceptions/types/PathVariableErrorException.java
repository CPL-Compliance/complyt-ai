package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

import java.util.List;

@Generated
public class PathVariableErrorException extends ComplytApiException {

    public PathVariableErrorException() {
        super(GenericErrorMessages.PATH_VARIABLE_DEFAULT_ERROR);
    }

    public PathVariableErrorException(List<String> errorList) {
        super(errorList.toString());
    }
}
