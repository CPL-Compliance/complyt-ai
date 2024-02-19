package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Patcher<T> {

    private final Map<String, BiFunction<T, Object, T>> fieldsToFunctions;

    public T patch(@NonNull T object, @NonNull Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object o = map.get(key);
            object = fieldsToFunctions.get(key).apply(object, o);
        }

        return object;
    }

}