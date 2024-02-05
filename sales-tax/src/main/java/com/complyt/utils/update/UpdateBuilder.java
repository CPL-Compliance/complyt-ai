package com.complyt.utils.update;

import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Update;

public interface UpdateBuilder<T> {

    Update build(@NonNull T t);
}
