package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.InvalidPatchFieldException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Patcher<T> {

    private final Map<String, BiFunction<T, Object, T>> fieldsToFunctions;

    public T patch(@NonNull T object, @NonNull Map<String, Object> map) {
        try {
            for (String key : map.keySet()) {
                Object o = map.get(key);
                object = fieldsToFunctions.get(key).apply(object, o);
            }

            return object;
        } catch (Exception exception) {
            log.info("The requested operation failed because of an invalid patch field provided.");
        }
        return null;
    }

}