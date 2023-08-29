package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.StateDto;
import jakarta.validation.Valid;

import java.util.List;

public record ExemptionWrapperDto(@Valid ExemptionDto exemptionDto, List<StateDto> states) {

}