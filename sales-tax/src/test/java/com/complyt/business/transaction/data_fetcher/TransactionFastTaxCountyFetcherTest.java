package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionFastTaxCountyFetcherTest {

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;
    UnitTestUtilities testUtilities;
    @InjectMocks
    private TransactionFastTaxCountyFetcher transactionFastTaxCountyFetcher;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "injectedCounty", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
        // Given
        TaxInfoItem taxInfoItem = createTaxInfoItem();
        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        FastTaxData fastTaxData = new FastTaxData("0", taxInfoItems);
        Transaction transactionWithInjectedCounty = transaction
                .withShippingAddress(transaction.getShippingAddress()
                        .withCounty(fastTaxData.getTaxInfoItems().get(0).getCounty()));

        // When
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        Mono<String> countyMono = transactionFastTaxCountyFetcher.fetch(transaction.getShippingAddress());

        // Then
        StepVerifier.create(countyMono).expectNext(transactionWithInjectedCounty.getShippingAddress().getCounty()).verifyComplete();
    }

    @Test
    void equals_SameTransactionFastTaxCountyFetcher_ReturnsTrue() {
        // Given
        TransactionFastTaxCountyFetcher givenTransactionFastTaxCountyFetcher = transactionFastTaxCountyFetcher;

        // When
        boolean isEquals = transactionFastTaxCountyFetcher.equals(givenTransactionFastTaxCountyFetcher);

        // Then
        assertTrue(isEquals);
    }
}
