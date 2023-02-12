package com.complyt.v1.models.properties;

import java.util.UUID;

public interface ComplytIdPropertyDto {
    UUID complytId();
    ComplytIdPropertyDto withComplytId(UUID complytId);
}
