package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;

import java.util.List;

@Generated
public class ConflictedDataApiException extends ComplytApiException {

    public ConflictedDataApiException(List<String> list) {
        super(list.toString());
    }
}