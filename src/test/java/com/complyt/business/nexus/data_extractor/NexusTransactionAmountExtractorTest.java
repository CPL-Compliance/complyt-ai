package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemStateThresholdQualifier;
import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusTransactionAmountExtractorTest {

    @InjectMocks
    NexusTransactionAmountExtractor nexusTransactionAmountExtractor;

    @Mock
    ItemStateThresholdQualifier itemStateThresholdQualifier;

    Transaction transaction;
    NexusStateRule nexusStateRule;
    Customer customer;
    ObjectId customerId;

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        transaction = createTransaction();
        nexusStateRule = createNexusStateRule();
    }

    private Customer createCustomer() {
        customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, clientId, CustomerType.RETAIL, null);
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<TangibleCategory>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<CustomerType>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
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
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, clientId, null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE);
    }

    @Test
    void extract_ExtractsTransactionItemsAmount_ReturnsAmount() {
        // Given

        // When
        when(itemStateThresholdQualifier.isCounted(transaction.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(itemStateThresholdQualifier.isCounted(transaction.getItems().get(1), nexusStateRule)).thenReturn(false);
        float amount = nexusTransactionAmountExtractor.extract(transaction, nexusStateRule);

        // Then
        assertEquals(amount, transaction.getItems().get(0).getTotalPrice());
    }

    @Test
    void extract_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionAmountExtractor.extract(nullTransaction, nexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void extract_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionAmountExtractor.extract(transaction, nullNexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

}
