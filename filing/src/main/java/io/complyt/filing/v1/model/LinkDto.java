package io.complyt.filing.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
@Schema(name = "Link")
public class LinkDto {
    private final String link;
}
