package com.complyt.v1.model.customer;

import com.complyt.domain.customer.exemption.ExemptionType;
import com.complyt.v1.model.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "Customer")
public class CustomerDto {
    private final String id;
    private final String externalId;
    private final String name;
    private final AddressDto address;
    private final CustomerTypeDto customerType;
    private final Map<String, ExemptionType> exemptionsStates;
}
