package com.complyt.business.sales_tax.checker;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerFullyExemptionCheckTest {

    CustomerFullyExemptionCheck customerFullyExemptionCheck;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        customerFullyExemptionCheck = new CustomerFullyExemptionCheck(transaction);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "CA", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Customer customer = new Customer(customerId.toString(), UUID.randomUUID().toString(), "name", null, clientId, CustomerType.RETAIL, null);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, new ObjectId(), customer, null, TransactionStatus.ACTIVE, clientId, null, externalTimeStamps);
    }

    @Test
    void check_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerFullyExemptionCheck.check(nullExemption);
        });

        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void isFullyExemptionActive_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerFullyExemptionCheck.isFullyExemptionActive(nullExemption);
        });

        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

}
