package io.complyt.files.domain;

import lombok.With;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@With
public record ComplytFileMetadata(UUID complytId, Map<String, String> metadata,
                                  String tenantId, OffsetDateTime updateTime,
                                  OffsetDateTime createTime, String link) {
}
