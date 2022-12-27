package io.complyt.filing.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "link")
public class Link {
    @Id
    private final String id;

    private final String tenantId;

    private final String link;
}
