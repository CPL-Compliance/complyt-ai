package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;

import java.util.List;

public record ExemptionWrapper(Exemption exemption, List<State> states) {
}