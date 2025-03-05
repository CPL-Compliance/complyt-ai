package com.complyt.repositories.internal_rates;

import com.complyt.domain.Address;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.repositories.QueryBuilder;
import com.complyt.repositories.internal_rates.address_standardization.StandardizeAddress;
import com.complyt.repositories.internal_rates.criteria.CountyZipCriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
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

    public Query build(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("address.zip").is(internalSalesTaxRates.getAddress().zip()),
                Criteria.where("address.lowerPlusFourDigits").is(internalSalesTaxRates.getAddress().lowerPlusFourDigits()),
                Criteria.where("address.upperPlusFourDigits").is(internalSalesTaxRates.getAddress().upperPlusFourDigits())
        );

        log.info("find internal rate with Criteria:" + new Query().addCriteria(criteria));
        return new Query().addCriteria(criteria);
    }

}