package com.complyt.v1.exceptions;

import com.complyt.annotations.Generated;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Generated
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", error.getMessage());
        errorMap.put("endpoint url ", request.path());

        return errorMap;
    }
}