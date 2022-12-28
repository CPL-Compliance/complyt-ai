package io.complyt.files.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "file")
public class File {
    @Id
    @NonNull
    private final String id;

    @NonNull
    private final String tenantId;

    @NonNull
    private final String link;
}