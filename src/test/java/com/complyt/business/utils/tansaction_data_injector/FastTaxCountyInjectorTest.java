package com.complyt.business.utils.tansaction_data_injector;

import com.complyt.business.utils.transaction_data_injector.FastTaxCountyInjector;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FastTaxCountyInjectorTest {

    private FastTaxCountyInjector fastTaxCountyInjector;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
//        fastTaxCountyInjector = new FastTaxCountyInjector();
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
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

//    @Test
//    void inject_InjectsCounty_ReturnsOrder() {
//        List<TaxInfoItem> taxInfoItems = new ArrayList<TaxInfoItem>(){{add(new TaxInfoItem());}};
//        FastTaxData fastTaxData = new FastTaxData("0",taxInfoItems);
//        Transaction actualTransaction = fastTaxCountyInjector.inject(transaction,fastTaxData);
//
//        assertEquals(actualTransaction.getShippingAddress().getCounty(),null);
//    }
}
