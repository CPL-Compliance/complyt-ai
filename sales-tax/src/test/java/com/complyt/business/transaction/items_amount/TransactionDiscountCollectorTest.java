package com.complyt.business.transaction.items_amount;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.transaction.items_amounts.AmountCalculator;
import com.complyt.business.transaction.items_amounts.TransactionDiscountCollector;
import com.complyt.business.transaction.items_amounts.TransactionItemsAmountsCollector;
import com.complyt.domain.Discountable;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionDiscountCollectorTest {
    @Mock
    private CollectionBuilder<Discountable> discountableCollectionBuilder;

    @Mock
    AmountCalculator<List<Discountable>> discountablesTotalDiscountCalculator;

    @InjectMocks
    private TransactionDiscountCollector transactionDiscountCollector;

    private Transaction transaction;
    private List<Discountable> items;
    private UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(new ObjectId().toString());
        items = new ArrayList<>(transaction.getItems());
    }


    @Test
    public void collect_CollectsAllAmountsAllDiscountIsNull_ReturnsTransactionWith0TotalDiscount() {
        // Before + When
        when(discountableCollectionBuilder.build(transaction)).thenReturn(items);
        when(discountablesTotalDiscountCalculator.calculate(items)).thenReturn(BigDecimal.ZERO);

        // Then
        Transaction outputTransaction = transactionDiscountCollector.collect(transaction);
        assertEquals(BigDecimal.ZERO, outputTransaction.getTotalDiscount());
    }

    @Test
    public void collect_CollectsAllAmounts1ItemHasDiscount_ReturnsTransactionWithTotalDiscountOf1Item() {
        // Given
        List<Item> itemList = testUtilities.createItems(true, false,true);
        List<Item> itemListWith1Discount = List.of(
                itemList.get(0).withDiscount(BigDecimal.valueOf(500)),
                itemList.get(1).withDiscount(null)
        );

        Transaction transactionWithOneItemDiscount = transaction.withItems(itemListWith1Discount);
        List<Discountable> discountableList = new ArrayList<>(transactionWithOneItemDiscount.getItems());

        BigDecimal expectedValue = itemListWith1Discount.get(0)
                .getDiscount();
        // When
        when(discountableCollectionBuilder.build(transactionWithOneItemDiscount))
                .thenReturn(discountableList);
        when(discountablesTotalDiscountCalculator.calculate(discountableList)).thenReturn(BigDecimal.valueOf(500));

        // Then
        Transaction outputTransaction = transactionDiscountCollector.collect(transactionWithOneItemDiscount);
        assertEquals(expectedValue, outputTransaction.getTotalDiscount());
    }

    @Test
    public void collect_CollectsAllAmounts2ItemHasDiscount_ReturnsTransactionWithTotalDiscountOf1Item() {
        // Given
        List<Item> itemList = testUtilities.createItems(true, false,true);
        List<Item> itemListWith2Discount = List.of(
                itemList.get(0).withDiscount(BigDecimal.valueOf(500)),
                itemList.get(1).withDiscount(BigDecimal.valueOf(400))
        );
        Transaction transactionWithTwoItemDiscount = transaction.withItems(itemListWith2Discount);
        List<Discountable> discountableList = new ArrayList<>(transactionWithTwoItemDiscount.getItems());

        BigDecimal expectedValue = itemListWith2Discount.get(0).getDiscount()
                .add(itemListWith2Discount.get(1).getDiscount());
        // When
        when(discountableCollectionBuilder.build(transactionWithTwoItemDiscount))
                .thenReturn(discountableList);
        when(discountablesTotalDiscountCalculator.calculate(discountableList)).thenReturn(BigDecimal.valueOf(900));

        // Then
        Transaction outputTransaction = transactionDiscountCollector.collect(transactionWithTwoItemDiscount);
        assertEquals(expectedValue, outputTransaction.getTotalDiscount());
    }

    @Test
    public void collect_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> transactionDiscountCollector.collect(nullTransaction));

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

}