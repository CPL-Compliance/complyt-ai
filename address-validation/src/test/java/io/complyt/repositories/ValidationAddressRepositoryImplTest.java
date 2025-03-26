package io.complyt.repositories;

import io.complyt.business.collection_fetcher.UsaStatesMap;
import io.complyt.domain.Address;
import io.complyt.domain.AddressQueryBuilder;
import io.complyt.domain.ValidatedAddress;
import io.complyt.security.TenantResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidationAddressRepositoryImplTest {

    @InjectMocks
    private ValidationAddressRepositoryImpl validationAddressRepository;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    private AddressQueryBuilder addressQueryBuilder;

    @Mock
    TenantResolver tenantResolver;

    private final Address address = TestUtilities.getAddress();
    private final ValidatedAddress validatedAddress = TestUtilities.getValidatedAddress();
    private final String collection = UsaStatesMap.statesToCollections.get(address.state());


    @Test
    void saveAddress_ValidAddress_ReturnsSavedAddress() {
        when(reactiveMongoTemplate.save(validatedAddress, collection))
                .thenReturn(Mono.just(validatedAddress));
        when(tenantResolver.resolve()).thenReturn(Mono.just("12345"));

        StepVerifier.create(validationAddressRepository.saveAddress(validatedAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void findAddress_AddressFound_ReturnsValidatedAddress() {
        Query query = new Query();
        when(addressQueryBuilder.build(address)).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, ValidatedAddress.class, collection))
                .thenReturn(Mono.just(validatedAddress));
        when(tenantResolver.resolve()).thenReturn(Mono.just("12345"));

        StepVerifier.create(validationAddressRepository.findAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void findAddress_AddressNotFound_ReturnsEmpty() {
        Query query = new Query();
        when(addressQueryBuilder.build(address)).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, ValidatedAddress.class, collection))
                .thenReturn(Mono.empty());
        when(tenantResolver.resolve()).thenReturn(Mono.just("12345"));

        StepVerifier.create(validationAddressRepository.findAddress(address))
                .verifyComplete();
    }

    @Test
    void saveAddress_NullValidatedAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validationAddressRepository.saveAddress(null));
    }

    @Test
    void findAddress_NullAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validationAddressRepository.findAddress(null));
    }
}