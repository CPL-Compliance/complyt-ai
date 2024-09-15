package com.complyt.utils.query;

import com.complyt.domain.transaction.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;


public interface CountryAndStateCriteriaBuilder {

    Criteria build(@NonNull Address address);
    Criteria build(@NonNull String country, String state);
}
