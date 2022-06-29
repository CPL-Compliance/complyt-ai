package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.v1.model.TransactionDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class TransactionMapperTest {

    @Test
    void transactionDtoToTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000,4,8000,"description","name","taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0
                ));
            }
        };

        Transaction transaction = new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null,null, TransactionStatus.ACTIVE, clientId);
        TransactionDto transactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(transaction);

    }

    @Test
    void transactionToTransactionDto() {
    }

    @Test
    void testTransactionToTransactionDto() {
    }
}