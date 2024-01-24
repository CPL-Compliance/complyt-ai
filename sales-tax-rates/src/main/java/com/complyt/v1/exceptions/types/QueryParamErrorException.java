package com.complyt.v1.exceptions.types;

import java.util.List;

public class QueryParamErrorException extends ComplytApiException {

    public QueryParamErrorException(List<String> errorList) {
        super(errorList.toString());
    }
}