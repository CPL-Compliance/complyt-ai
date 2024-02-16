package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Patcher<T> {

    Map<String, BiFunction<T, Object, T>> fieldsToFunctions;

    public T patch(T object, Map<String, Object> values) {

        for (String key : values.keySet()) {
            Object o = values.get(key);
            object = fieldsToFunctions.get(key).apply(object, o);
        }

        return object;
    }

}