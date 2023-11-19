package io.complyt.authentication.domain;

import lombok.NonNull;

public record ApiKey(@NonNull String clientId, @NonNull String clientSecret) {

}
