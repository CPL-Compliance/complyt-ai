package com.complyt.utils.query;

import com.complyt.domain.State;
import com.complyt.domain.transaction.Address;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


public interface CountryAndStateCriteriaBuilder {

    Criteria build(@NonNull Address address);
    Criteria build(@NonNull String country, String state);
}
