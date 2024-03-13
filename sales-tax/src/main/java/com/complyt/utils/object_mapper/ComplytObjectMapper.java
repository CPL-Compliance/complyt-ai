package com.complyt.utils.object_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ComplytObjectMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    /*
        This function receives a generic object and the type to convert it to
        and returns the concrete object by its type
     */
    static Object mapObject(Object o, Class patchingClass) {
        try {
            return objectMapper.convertValue(o, patchingClass);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /*
        This will be uncommented as soon as we will implement patch to transaction
     */
//    static <T> List<T> convertToList(Object objectList, Class patchingClass) {
//        List<T> returnedObjects = new ArrayList<>();
//        List<Object> objectsToMap = (List<Object>) objectList;
//
//        for (Object objectToMap : objectsToMap) {
//            Object mappedObject = mapObject(objectToMap, patchingClass);
//            returnedObjects.add((T) mappedObject);
//        }
//
//        return returnedObjects;
//    }

}