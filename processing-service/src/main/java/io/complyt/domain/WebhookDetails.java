package io.complyt.domain;

import lombok.With;

@With
public record WebhookDetails(Boolean shouldForwardWriteOperations, String host, String path) {}
