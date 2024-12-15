package com.complyt.repositories.internal_rates.criteria;

import com.complyt.domain.Address;
import org.springframework.data.mongodb.core.query.Criteria;

public interface CriteriaBuilder {
    Criteria build(Address address);
}
