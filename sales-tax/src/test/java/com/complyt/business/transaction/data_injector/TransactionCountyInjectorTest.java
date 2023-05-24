package com.complyt.business.transaction.data_injector;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionCountyInjectorTest {

    TransactionCountyInjector transactionCountyInjector;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        transactionCountyInjector = new TransactionCountyInjector(transaction);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0
                        , TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE));
            }
        };

        return Transaction.builder()
                .id(id)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .tenantId(tenantId)
                .transactionStatus(TransactionStatus.ACTIVE)
                .build();
    }

    @Test
    void defaultConstructor_Transaction_ReturnTransactionCountyInjector() {
        // Given + When
        TransactionCountyInjector injector = new TransactionCountyInjector(transaction);

        // Then
        assertEquals(transaction, injector.getTransaction());
    }

    @Test
    void inject_DifferentCounty_TransitionModified() {
        // Given
        Transaction expectedTransition = transaction.withShippingAddress(transaction.getShippingAddress().withCounty("New County"));

        // When
        Mono<Transaction> transactionMono = transactionCountyInjector.inject("New County");

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransition).verifyComplete();
    }
}