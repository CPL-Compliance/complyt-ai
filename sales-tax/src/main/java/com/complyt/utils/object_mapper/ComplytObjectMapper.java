package com.complyt.utils.object_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public interface ComplytObjectMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    static Object mapObject(Object o, Class patchingClass) {
        try {
            return objectMapper.convertValue(o, patchingClass);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    static <T> List<T> convertToList(Object objectList, Class patchingClass) {
        List<T> returnedObjects = new ArrayList<>();
        List<Object> objectsToMap = (List<Object>) objectList;

        for (Object objectToMap : objectsToMap) {
            Object mappedObject = mapObject(objectToMap, patchingClass);
            returnedObjects.add((T) mappedObject);
        }

        return returnedObjects;
    }

}