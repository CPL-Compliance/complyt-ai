package com.complyt.config.web_clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.javatuples.Pair;

@Getter
@Builder
@AllArgsConstructor
public class WebClientWrapperProperties {
    @NonNull
    private final String scheme;
    @NonNull
    private final String host;
    @NonNull
    private final String path;
    @NonNull
    private final Pair<String, String> key;
}
