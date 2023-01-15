package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.CountyFetcher;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.bson.types.ObjectId;
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
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CountyProviderTest {

    @InjectMocks
    CountyProvider countyProvider;

    @Mock
    CountyFetcher countyFetcher;
    DomainObjectStub domainObjectStub;

    @BeforeEach void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());

    }

    @Test
    void provide_GetsCountyAndInjectsIt_ReturnsTransaction() {
        // Given
        Transaction transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
        String injectedCounty = "InjectedCounty";
        Transaction transactionWithCounty = transaction.withShippingAddress(transaction.getShippingAddress().withCounty(injectedCounty));

        // When
        when(countyFetcher.fetch(transaction.getShippingAddress())).thenReturn(Mono.just(injectedCounty));
        Mono<Transaction> transactionMono = countyProvider.provide(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithCounty).verifyComplete();
    }
}
