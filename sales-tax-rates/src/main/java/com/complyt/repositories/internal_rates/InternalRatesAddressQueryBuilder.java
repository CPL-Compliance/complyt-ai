package com.complyt.repositories.internal_rates;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithDate;
import com.complyt.repositories.QueryBuilder;
import com.complyt.repositories.internal_rates.address_standardization.StandardizeAddress;
import com.complyt.repositories.internal_rates.criteria.CountyZipCriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InternalRatesAddressQueryBuilder implements QueryBuilder<Address> {

    @NonNull
    private final StandardizeAddress standardizeAddress;

    @NonNull
    private final CountyZipCriteriaBuilder countyCityZipCriteriaBuilder;
    
    @Override
    public Query build(@NonNull Address address) {
        Address standardizedAddress = standardizeAddress.standardize(address);
        return Query.query(countyCityZipCriteriaBuilder.build(standardizedAddress));
    }
}