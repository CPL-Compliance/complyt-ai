package com.complyt.domain.customer;

import java.util.UUID;

public record CustomerLookupDetail(UUID customerId, String customerExternalReference, String customerSource) {
}
