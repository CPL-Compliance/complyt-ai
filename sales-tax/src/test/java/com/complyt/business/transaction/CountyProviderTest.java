package com.complyt.business.transaction;

import com.complyt.business.transaction.data_fetcher.CountyFetcher;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.bson.types.ObjectId;
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
public class CountyProviderTest {

    @InjectMocks
    CountyProvider countyProvider;

    @Mock
    CountyFetcher countyFetcher;

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

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId.toString(), null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE, null, null);
    }

    @Test
    void provide_GetsCountyAndInjectsIt_ReturnsTransaction() {
        // Given
        Transaction transaction = createTransaction();
        String injectedCounty = "InjectedCounty";
        Transaction transactionWithCounty = transaction.withShippingAddress(transaction.getShippingAddress().withCounty(injectedCounty));

        // When
        when(countyFetcher.fetch(transaction.getShippingAddress())).thenReturn(Mono.just(injectedCounty));
        Mono<Transaction> transactionMono = countyProvider.provide(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithCounty).verifyComplete();
    }
}
