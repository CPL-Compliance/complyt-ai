package com.complyt.v1.models.customer;

import com.complyt.v1.models.AddressDto;
import com.complyt.v1.models.properties.ExternalIdPropertyDto;
import com.complyt.v1.models.properties.SourcePropertyDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "Customer")
public record CustomerDto(UUID complytId,
                          @NotBlank(message = "External ID may not be blank") @Size(min = 1, max = 256, message = "External ID should be 1-256 characters maximum") String externalId,
                          @NotBlank(message = "Source may not be blank") @Pattern(regexp = "[1-9]", message = "Source should be a single digit") String source,
                          @NotBlank(message = "Name may not be blank") @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum") String name,
                          @Valid @NotNull(message = "Address may not be null") AddressDto address,
                          @NotNull(message = "Customer type may not be null") CustomerTypeDto customerType,
                          @Valid TimestampsDto internalTimestamps,
                          @Valid /*@NotNull(message = "External timestamps may not be null")*/ TimestampsDto externalTimestamps
) implements ExternalIdPropertyDto, SourcePropertyDto {
}