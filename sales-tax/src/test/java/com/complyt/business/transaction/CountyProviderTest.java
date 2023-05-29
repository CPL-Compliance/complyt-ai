package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.AddressFetcher;
import com.complyt.domain.Transaction;
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
public class CountyProviderTest {

    @InjectMocks
    CountyProvider countyProvider;

    @Mock
    AddressFetcher countyFetcher;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());

    }

    @Test
    void provide_GetsCountyAndInjectsIt_ReturnsTransaction() {
        // Given
        Transaction transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        String injectedCounty = "InjectedCounty";
        Transaction transactionWithCounty = transaction.withShippingAddress(transaction.getShippingAddress().withCounty(injectedCounty));

        // When
        when(countyFetcher.fetch(transaction.getShippingAddress())).thenReturn(Mono.just(injectedCounty));
        Mono<Transaction> transactionMono = countyProvider.provide(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithCounty).verifyComplete();
    }
}
