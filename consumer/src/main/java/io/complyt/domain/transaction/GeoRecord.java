package io.complyt.domain.transaction;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@With
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "geo_records")
@Data
@Accessors(chain = true)
public class GeoRecord {

    @Id
    String id;
    String zip;
    String state;
}