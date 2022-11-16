package com.complyt.config.web_clients;

import lombok.*;
import org.javatuples.Pair;

@Getter
@Builder
@EqualsAndHashCode
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

    public static WebClientWrapperProperties WebClientWrapperPropertiesStub(){
        return new WebClientWrapperProperties("", "", "", new Pair<>("", ""));
    }
}
