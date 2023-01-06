package com.complyt.v1.model.customer;

import com.complyt.annotations.Default;
import com.complyt.domain.customer.Customer;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ComplytEntityDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Schema(name = "Customer")
public class CustomerDto extends ComplytEntityDto {
    private final String id;
    private final String externalId;
    private final String source;
    private final String name;
    private final AddressDto address;
    private final CustomerTypeDto customerType;
    private final TimestampsDto internalTimestamps;
    private final TimestampsDto externalTimestamps;

    @Default
    public CustomerDto(final UUID complytId, final String id, final String externalId, final String source, final String name, final AddressDto address, final CustomerTypeDto customerType, final TimestampsDto internalTimestamps, final TimestampsDto externalTimestamps) {
        super(complytId);
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.name = name;
        this.address = address;
        this.customerType = customerType;
        this.internalTimestamps = internalTimestamps;
        this.externalTimestamps = externalTimestamps;
    }

    @Override
    public ComplytEntityDto withComplytId(UUID complytId) {
        return this.complytId == complytId ?
                this : new CustomerDto(
                complytId,
                this.id,
                this.externalId,
                this.source,
                this.name,
                this.address,
                this.customerType,
                this.internalTimestamps,
                this.externalTimestamps);
    }
}
