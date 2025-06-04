package io.complyt.domain.nexus;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
@Data
@Accessors(chain = true)
public class PhysicalNexusTracker {
    private boolean established;
    private LocalDateTime establishedDate;
}
