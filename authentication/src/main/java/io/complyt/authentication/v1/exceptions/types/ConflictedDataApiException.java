package io.complyt.authentication.v1.exceptions.types;


import io.complyt.authentication.annotations.Generated;

import java.util.List;

@Generated
public class ConflictedDataApiException extends ComplytApiException {

    public ConflictedDataApiException(List<String> list) {
        super(list.toString());
    }
}