package io.complyt.files.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "file")
public class File {

    @NonNull
    UUID complytId;
    @Id
    @NonNull
    String id;
    @NonNull
    String tenantId;

    @NonNull
    String link;
}