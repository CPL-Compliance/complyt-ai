package com.complyt.business.utils.data_injector;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.business.data_injector.TransactionCustomerInjector;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCustomerInjectorTest {

    TransactionCustomerInjector transactionCustomerInjector;

    @BeforeEach
    void setUp() {
        Transaction transaction = createTransaction();
        transactionCustomerInjector = new TransactionCustomerInjector(transaction);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();

        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "CA", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, null, null, null, TransactionStatus.ACTIVE, null, null, externalTimeStamps, TransactionType.INVOICE);
    }

    @Test
    void inject_NullCustomerPassed_ThrowsException() {
        // Given
        Customer nullCustomer = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionCustomerInjector.inject(nullCustomer));

        // Then
        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");

    }
}
