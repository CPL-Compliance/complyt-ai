package io.complyt.files.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@Schema(name = "File")
public class FileDto {
    @NonNull
    @Max(2048)
    private final String link;
}
