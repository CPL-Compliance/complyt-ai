package com.complyt.utils;

import com.complyt.business.sales_tax.checker.TaxableItemExistenceCheck;
import com.complyt.business.sales_tax.sales_tax_amount.ISalesTaxCalculator;
import com.complyt.business.sales_tax.sales_tax_amount.ItemsSalesTaxCalculator;
import com.complyt.business.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.sales_tax.sales_tax_amount.ShippingFeeSalesTaxCalculator;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.utils.factory.SalesTaxAggregatorFactory;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SalesTaxAggregatorFactoryTest {

    @InjectMocks
    SalesTaxAggregatorFactory salesTaxAggregatorFactory;

    @Mock
    TaxableItemExistenceCheck taxableItemExistence;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }


    private Transaction createTransaction() {
        String id = null;
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee);
    }

    @Test
    void createSalesTaxAggregator_CreatesAggregatorWithItemAndShippingCalculators_ReturnsAggregator() {
        // Given
        List<ISalesTaxCalculator> onlyItemCalculatorList = new ArrayList<>() {{
            add(new ItemsSalesTaxCalculator(transaction.getItems()));
            add(new ShippingFeeSalesTaxCalculator(transaction.getShippingFee()));
        }};
        SalesTaxAggregator expectedSalesTaxAggregator = new SalesTaxAggregator(onlyItemCalculatorList);

        // When
        when(taxableItemExistence.hasTaxableItem(transaction.getItems())).thenReturn(true);
        SalesTaxAggregator actualSalesTaxAggregator = new SalesTaxAggregatorFactory(taxableItemExistence).createSalesTaxAggregator(transaction);

        // Then
        assertEquals(expectedSalesTaxAggregator, actualSalesTaxAggregator);
    }

    @Test
    void createSalesTaxAggregator_NoTaxableItems_DoesNotInitializeShippingFeeTaxCalculator() {
        // Given
        List<ISalesTaxCalculator> onlyItemCalculatorList = new ArrayList<>() {{
            add(new ItemsSalesTaxCalculator(transaction.getItems()));
        }};
        SalesTaxAggregator expectedSalesTaxAggregator = new SalesTaxAggregator(onlyItemCalculatorList);

        // When
        when(taxableItemExistence.hasTaxableItem(transaction.getItems())).thenReturn(false);
        SalesTaxAggregator actualSalesTaxAggregator = new SalesTaxAggregatorFactory(taxableItemExistence).createSalesTaxAggregator(transaction);

        // Then
        assertEquals(expectedSalesTaxAggregator, actualSalesTaxAggregator);
    }

    @Test
    void createSalesTaxAggregator_ShippingFeeIsNull_DoesNotInitializeShippingFeeTaxCalculator() {
        // Given
        List<ISalesTaxCalculator> onlyItemCalculatorList = new ArrayList<>() {{
            add(new ItemsSalesTaxCalculator(transaction.getItems()));
        }};
        Transaction transactionWithNullShippingFee = transaction.withShippingFee(null);
        SalesTaxAggregator expectedSalesTaxAggregator = new SalesTaxAggregator(onlyItemCalculatorList);

        // When
        SalesTaxAggregator actualSalesTaxAggregator = new SalesTaxAggregatorFactory(taxableItemExistence).createSalesTaxAggregator(transactionWithNullShippingFee);

        // Then
        assertEquals(expectedSalesTaxAggregator, actualSalesTaxAggregator);
    }

    @Test
    void createSalesTaxAggregator_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxAggregatorFactory.createSalesTaxAggregator(nullTransaction);
        });

        // Then
        assertEquals("transaction is marked non-null but is null", nullPointerException.getMessage());
    }

}