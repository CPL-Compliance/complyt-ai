package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.CityCountyFetcher;
import com.complyt.business.transaction.data_injector.TransactionCityCountyInjector;
import com.complyt.domain.transaction.CityCountyWrapper;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CityCountyProviderTest {

    @InjectMocks
    CityCountyProvider cityCountyProvider;

    @Mock
    CityCountyFetcher addressFetcher;

    @Mock
    TransactionCityCountyInjector transactionCityCountyInjector;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());

    }

    @Test
    void provide_GetsAddressAndInjectsIt_ReturnsTransaction() {
        // Given
        Transaction transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(transaction.getShippingAddress().city(), transaction.getShippingAddress().county());

        // When
        when(addressFetcher.fetch(transaction.getShippingAddress())).thenReturn(Mono.just(cityCountyWrapper));
        when(transactionCityCountyInjector.inject(cityCountyWrapper, transaction)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = cityCountyProvider.provide(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

}