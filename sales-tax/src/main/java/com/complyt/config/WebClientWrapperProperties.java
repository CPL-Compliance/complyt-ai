package com.complyt.config;

import lombok.*;

@Builder
public record WebClientWrapperProperties(@NonNull String scheme, @NonNull String host, @NonNull String path) {
    public static WebClientWrapperProperties WebClientWrapperPropertiesStub() {
        return new WebClientWrapperProperties("", "", "");
    }
}
