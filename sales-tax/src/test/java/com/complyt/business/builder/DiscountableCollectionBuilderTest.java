package com.complyt.business.builder;

import com.complyt.domain.Discountable;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)

class DiscountableCollectionBuilderTest {

    @InjectMocks
    DiscountableCollectionBuilder discountableCollectionBuilder;

    Transaction transaction;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void build_TransactionWithItems_ReturnDiscountableListOfItems() {
        // Given
        List<Discountable> expectedDiscountables = new ArrayList<>(transaction.getItems());

        // When
        List<Discountable> actualDiscountable = (List<Discountable>) discountableCollectionBuilder.build(transaction);

        // Then
        Assertions.assertEquals(expectedDiscountables, actualDiscountable);
    }
}