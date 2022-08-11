package com.complyt.business.utils.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionFastTaxCountyFetcherTest {

    @InjectMocks
    private TransactionFastTaxCountyFetcher transactionFastTaxCountyFetcher;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "CA", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId, null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()));
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "injectedCounty", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void inject_InjectsCounty_ReturnsOrder() {
        // Given
        TaxInfoItem taxInfoItem = createTaxInfoItem();
        List<TaxInfoItem> taxInfoItems = new ArrayList<TaxInfoItem>() {{
            add(taxInfoItem);
        }};
        FastTaxData fastTaxData = new FastTaxData("0", taxInfoItems);
        Transaction transactionWithInjectedCounty = transaction
                .withShippingAddress(transaction.getShippingAddress()
                        .withCounty(fastTaxData.getTaxInfoItems().get(0).getCounty()));

        // When
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        Mono<Transaction> transactionMono = transactionFastTaxCountyFetcher.fetch(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithInjectedCounty).verifyComplete();
    }
}
