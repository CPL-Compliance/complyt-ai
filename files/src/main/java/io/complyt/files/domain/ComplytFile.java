package io.complyt.files.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.codec.multipart.FilePart;

@Getter
@Setter
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@EqualsAndHashCode
public class ComplytFile {
    FilePart file;
    ComplytFileMetadata metadata;
}
