package io.complyt.files.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;

@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
@With
@Schema(name = "File")
public class FileDto {
    @NonNull
    @Max(2048)
    String link;
}
