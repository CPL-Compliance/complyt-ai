package io.complyt.filing.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Schema(name = "Link")
public class LinkDto {
    @NonNull
    private final String link;
}
