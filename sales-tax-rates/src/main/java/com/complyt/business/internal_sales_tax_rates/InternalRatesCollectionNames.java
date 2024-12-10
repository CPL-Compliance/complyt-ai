package com.complyt.business.internal_sales_tax_rates;

import com.complyt.business.collection_fetcher.UsaStatesMap;

public interface InternalRatesCollectionNames {

    String POSIX_COLLECTION_NAME = "_internal_sales_tax_rates";

    static String stateInternalCollectionName(String state) {
        return UsaStatesMap.statesToCollections.get(state.toUpperCase()) + POSIX_COLLECTION_NAME;
    }
}
