package com.complyt.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "client_tracking")
public class ClientTracking {

    private final ObjectId clientId;
    private Nexus nexus;
}
