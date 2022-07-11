package com.complyt.domain.nexus.checker;

import lombok.NonNull;

public interface NexusCheck<T> {
    boolean check(@NonNull T t);
}
