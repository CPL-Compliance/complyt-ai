package com.complyt.v1.validators;

import com.complyt.v1.models.customer.exemption.ExemptionDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Patcher<T> {

    Map<String, BiFunction<T, Object, T>> fieldsToBuilders;

    public T patch(T object, Map<String, Object> values) {

        for (String key : values.keySet()) {
            Object o = values.get(key);
            object = fieldsToBuilders.get(key).apply(object, o);
        }

        return object;
    }

}