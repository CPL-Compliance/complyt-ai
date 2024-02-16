package com.complyt.utils.object_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ComplytObjectMapper {

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static Object mapObject(Object o, Class patchingClass) {
        try {
            return objectMapper.convertValue(o, patchingClass);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
