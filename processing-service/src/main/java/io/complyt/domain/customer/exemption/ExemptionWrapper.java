package io.complyt.domain.customer.exemption;

import io.complyt.domain.State;
import lombok.With;

import java.util.List;

@With
public record ExemptionWrapper(Exemption exemption, List<State> states) {
}