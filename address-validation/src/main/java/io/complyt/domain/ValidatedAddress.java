package io.complyt.domain;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Accessors(chain = true)
public class ValidatedAddress implements AddressData {
    @Id
    String id;
    CachedAddressData address;
    Address requestAddress;
    LocalDateTime createdDate;
}
