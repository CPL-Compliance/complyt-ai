package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ExemptionWrapperDto(
        @Valid @NotNull(message = "exemption " + DtoErrorMessages.NOT_NULL_ERROR) ExemptionDto exemption,
        @NotEmpty(message = "states " + DtoErrorMessages.LIST_NOT_EMPTY_ERROR) List<StateDto> states) {

}