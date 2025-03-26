package io.complyt.business.address;

import io.complyt.business.collection_fetcher.UsaStatesMap;
import io.complyt.domain.Address;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

class CollectionNameResolverTest {
    Address address = TestUtilities.getAddress();


    @Test
    void resolve_usaAddress_returnsUsaCollection() {
        String result = CollectionNameResolver.resolve(address);
        assertEquals(UsaStatesMap.statesToCollections.get(address.state()), result);
    }

    @Test
    void resolve_nonUsaAddress_returnsGlobalCollection() {
        address = TestUtilities.getAddress().withCountry("Canada");
        String result = CollectionNameResolver.resolve(address);
        assertEquals(CollectionNameResolver.GLOBAL_ADDRESSES_COLLECTION, result);
    }

    @Test
    void resolve_AddressWithNullCountryPassed_ThrowsNullPointerException() {
        // Given + When
        // Then
        assertThrows(NullPointerException.class, () -> CollectionNameResolver.resolve(null));
    }
}