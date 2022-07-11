package com.complyt.domain.nexus;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface NexusDataExtractor<T, Obj> {
    T extract(@NonNull Obj obj, @NonNull NexusStateRule nexusStateRule);
}
