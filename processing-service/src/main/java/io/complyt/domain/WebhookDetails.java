package io.complyt.domain;


public record WebhookDetails(Boolean shouldForwardWriteOperations, String host, String path) {}
