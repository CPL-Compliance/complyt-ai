package com.complyt.business.utils.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
import com.complyt.business.data_fetcher.TransactionZipTaxCountyFetcher;
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
class TransactionZipTaxCountyFetcherTest {

    @InjectMocks
    private TransactionZipTaxCountyFetcher transactionZipTaxCountyFetcher;

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
        ObjectId tenantId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId.toString(), null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE);
    }

    private Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
        // Given
        Result result = createResult();
        List<Result> results = new ArrayList<Result>() {{
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
}
