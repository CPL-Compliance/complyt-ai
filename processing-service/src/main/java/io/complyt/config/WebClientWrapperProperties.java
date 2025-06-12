package io.complyt.config;

import lombok.*;

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

    public static WebClientWrapperProperties WebClientWrapperPropertiesStub() {
        return new WebClientWrapperProperties("", "", "");
    }
}