package com.complyt.domain.fields;

import java.util.UUID;

public interface ComplytIdFieldDomain {
    UUID getComplytId();
    ComplytIdFieldDomain withComplytId(UUID complytId);
}
