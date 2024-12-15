package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class AddressWithDate implements TaxableLocation {
    Address address;
    LocalDateTime requiredDate;
}
