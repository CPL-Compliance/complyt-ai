package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

import java.util.Arrays;
import java.util.List;

@Generated
public class ConflictedDataApiException extends ComplytApiException {

    public ConflictedDataApiException() {
        super(GenericErrorMessages.DATA_CONFLICT_ERROR);
    }

    public ConflictedDataApiException(List<String> list) {
        super(list.stream()
                .collect(StringBuilder::new,
                        (builder, mergedErrors) -> builder.append("[" + mergedErrors + "]"),
                        (builder, error) -> builder.append(", " + error))
                .toString());
    }
}