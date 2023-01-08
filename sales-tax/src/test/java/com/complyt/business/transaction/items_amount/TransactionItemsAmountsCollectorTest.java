//package com.complyt.business.transaction.items_amount;
//
//import com.complyt.business.builder.CollectionBuilder;
//import com.complyt.business.transaction.items_amounts.TangibleItemsAmountCalculator;
//import com.complyt.business.transaction.items_amounts.TaxableItemsAmountCalculator;
//import com.complyt.business.transaction.items_amounts.TotalItemsAmountCalculator;
//import com.complyt.business.transaction.items_amounts.TransactionItemsAmountsCollector;
//import com.complyt.domain.*;
//import com.complyt.domain.nexus.enums.TangibleCategory;
//import com.complyt.domain.nexus.enums.TaxableCategory;
//import com.complyt.domain.sales_tax.SalesTaxRate;
//import com.complyt.domain.timestamps.ComplytTimestamp;
//import com.complyt.domain.timestamps.Timestamps;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class TransactionItemsAmountsCollectorTest {
//    @Mock
//    private CollectionBuilder<Taxable> taxableCollectionBuilder;
//
//    @Mock
//    private TaxableItemsAmountCalculator taxableItemsAmountCalculator;
//
//    @Mock
//    private TangibleItemsAmountCalculator tangibleItemsAmountCalculator;
//
//    @Mock
//    private TotalItemsAmountCalculator totalItemsAmountCalculator;
//
//    @InjectMocks
//    private TransactionItemsAmountsCollector transactionItemsAmountsCollector;
//
//    private Transaction createTransaction() {
//        String externalId = UUID.randomUUID().toString();
//        ObjectId customerId = new ObjectId();
//        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
//        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
//        String tenantId = UUID.randomUUID().toString();
//        List<Item> items = new ArrayList<>() {
//            {
//                add(new Item(2000, 4, 8000, "description", "name", "C1S1",
//                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE
//                ));
//            }
//        };
//        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
//        Timestamps timeStamps = new Timestamps(complytTimestamp, complytTimestamp);
//        ShippingFee shippingFee = null;
//        return new Transaction(UUID.randomUUID().toString(), externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null, 0, 0, 0);
//    }
//
//    @Test
//    public void testCollect() {
//        // Set up mocks
//        Transaction transaction = createTransaction();
//        List<Taxable> items = new ArrayList<>();
//        when(taxableCollectionBuilder.build(transaction)).thenReturn(items);
//        when(taxableItemsAmountCalculator.calculate(items)).thenReturn(10.0f);
//        when(tangibleItemsAmountCalculator.calculate(items)).thenReturn(5.0f);
//        when(totalItemsAmountCalculator.calculate(items)).thenReturn(15.0f);
//
//        // Test collect method
//        Transaction outputTransaction = transactionItemsAmountsCollector.collect(transaction);
//        assertEquals(10.0f, outputTransaction.getTaxableItemsAmount(), 0.001);
//        assertEquals(5.0f, outputTransaction.getTangibleItemsAmount(), 0.001);
//        assertEquals(15.0f, outputTransaction.getTotalItemsAmount(), 0.001);
//    }
//}
