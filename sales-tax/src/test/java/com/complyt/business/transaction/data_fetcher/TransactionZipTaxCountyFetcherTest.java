package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionZipTaxCountyFetcherTest {

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;
    @InjectMocks
    private TransactionZipTaxCountyFetcher transactionZipTaxCountyFetcher;
    private Transaction transaction;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
        // Given
        Result result = domainObjectStub.createResult();
        List<Result> results = new ArrayList<>() {{
            add(result);
        }};
        ZipTaxData zipTaxData = new ZipTaxData("version", 0, results);
        Transaction transactionWithInjectedCounty = transaction
                .withShippingAddress(transaction.getShippingAddress()
                        .withCounty(zipTaxData.getResults().get(0).getGeoCounty()));

        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(zipTaxData));
        Mono<String> countyMono = transactionZipTaxCountyFetcher.fetch(transaction.getShippingAddress());

        // Then
        StepVerifier.create(countyMono).expectNext(transactionWithInjectedCounty.getShippingAddress().getCounty()).verifyComplete();
    }

    @Test
    void equals_SameTransactionZipTaxCountyFetcher_ReturnsTrue() {
        // Given
        TransactionZipTaxCountyFetcher givenTransactionZipTaxCountyFetcher = transactionZipTaxCountyFetcher;

        // When
        boolean isEquals = transactionZipTaxCountyFetcher.equals(givenTransactionZipTaxCountyFetcher);

        // Then
        assertTrue(isEquals);
    }
}
