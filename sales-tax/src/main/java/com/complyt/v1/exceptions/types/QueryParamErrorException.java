package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;

import java.util.List;

@Generated
public class QueryParamErrorException extends ComplytApiException {

    public QueryParamErrorException(List<String> errorList) {
        super(errorList.toString());
    }
}
