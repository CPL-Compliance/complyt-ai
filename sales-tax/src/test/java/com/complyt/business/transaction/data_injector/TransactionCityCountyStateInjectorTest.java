package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionCityCountyStateInjectorTest {

    TransactionCityCountyStateInjector transactionCityCountyStateInjector;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        transactionCityCountyStateInjector = new TransactionCityCountyStateInjector(transaction);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip", false);
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "taxCode",
                        null, new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO
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
    void defaultConstructor_Transaction_ReturnTransactionCityCountyStateInjector() {
        // Given + When
        TransactionCityCountyStateInjector injector = new TransactionCityCountyStateInjector(transaction);

        // Then
        assertEquals(transaction, injector.transaction());
    }

    @Test
    void inject_DifferentCounty_TransitionModified() {
        // Given
        Address address = transaction.getShippingAddress().withCounty("New County");
        Transaction expectedTransition = transaction.withShippingAddress(address);
        CityCountyStateWrapper cityCountyStateWrapper = new CityCountyStateWrapper(address.city(), address.county(), address.state());

        // When
        Mono<Transaction> transactionMono = transactionCityCountyStateInjector.inject(cityCountyStateWrapper);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransition).verifyComplete();
    }
}