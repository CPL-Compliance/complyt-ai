package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import lombok.With;

import java.util.List;

@With
public record ExemptionWrapper(Exemption exemption, List<State> states) {
}