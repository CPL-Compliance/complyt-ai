package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;

import java.util.List;

@Generated
public class PathVariableErrorException extends ComplytApiException {

    public PathVariableErrorException(List<String> errorList) {
        super(errorList.toString());
    }
}