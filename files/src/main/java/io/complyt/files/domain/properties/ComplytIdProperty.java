package io.complyt.files.domain.properties;

import java.util.UUID;

public interface ComplytIdProperty {
    UUID getComplytId();

    ComplytIdProperty withComplytId(UUID complytId);
}
