package io.complyt.business.address;

import com.mongodb.lang.NonNull;
import io.complyt.business.collection_fetcher.UsaStatesMap;
import io.complyt.domain.Address;

public interface CollectionNameResolver {
    String GLOBAL_ADDRESSES_COLLECTION = "global_addresses";

    static String resolve(@NonNull Address address) {
        return CountryIsUsaChecker.isCountryUsa(address.country())
                ?  UsaStatesMap.statesToCollections.get(address.state().toUpperCase())
                : GLOBAL_ADDRESSES_COLLECTION;
    }
}
