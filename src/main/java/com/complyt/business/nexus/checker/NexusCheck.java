package com.complyt.business.nexus.checker;

import lombok.NonNull;

// comment
public interface NexusCheck<T> {
    boolean check(@NonNull T t);
}
