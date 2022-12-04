package com.complyt.business.sales_tax.checker;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.exemption.*;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerFullyExemptionCheckTest {

    CustomerFullyExemptionCheck customerFullyExemptionCheck;
    Transaction transaction;

    Exemption exemption;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        exemption = createExemption();
        customerFullyExemptionCheck = new CustomerFullyExemptionCheck(transaction);
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
    void check_TransactionFullyExempted_ReturnTrue() {
        // Given
        Exemption expectedExemption = exemption;

        // When
        boolean expectedBoolean = customerFullyExemptionCheck.check(expectedExemption);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void check_TransactionIsNotFullyExempted_ReturnFalse() {
        // Given
        Exemption expectedExemption = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When
        boolean expectedBoolean = customerFullyExemptionCheck.check(expectedExemption);

        // Then
        assertFalse(expectedBoolean);
    }

    @Test
    void check_TransactionIsBeforeTimeFrame_ReturnFalse() {
        // Given
        Exemption expectedExemption = exemption.withValidationDates(new ValidationDates(LocalDateTime.now().plusYears(2), LocalDateTime.now().plusYears(3)));

        // When
        boolean expectedBoolean = customerFullyExemptionCheck.check(expectedExemption);

        // Then
        assertFalse(expectedBoolean);
    }

    @Test
    void check_TransactionIsAfterTimeFrame_ReturnFalse() {
        // Given
        Exemption expectedExemption = exemption.withValidationDates(new ValidationDates(LocalDateTime.now().minusYears(2), LocalDateTime.now().minusYears(3)));

        // When
        boolean expectedBoolean = customerFullyExemptionCheck.check(expectedExemption);

        // Then
        assertFalse(expectedBoolean);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "CA", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Customer customer = new Customer(customerId.toString(), UUID.randomUUID().toString(), "name", null, tenantId, CustomerType.RETAIL);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, new ObjectId(), customer, null, TransactionStatus.ACTIVE, tenantId, null, externalTimeStamps, TransactionType.INVOICE, null);
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new ObjectId(),
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }


}
