package io.complyt.files.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
@With
@Schema(name = "File")
public class FileDto {

    @NonNull
    UUID complytId;
    @NonNull
    @Max(2048)
    String link;
}
