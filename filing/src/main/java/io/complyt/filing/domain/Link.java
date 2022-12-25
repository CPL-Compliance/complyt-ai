package io.complyt.filing.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@Document(collection = "link")
public class Link {
    @Id
    private final String id;

    @NonNull
    private final String tenantId;

    @NonNull
    private final String link;
}
