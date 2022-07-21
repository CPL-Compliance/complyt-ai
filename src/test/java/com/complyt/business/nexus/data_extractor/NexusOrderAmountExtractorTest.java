package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsCheck;
import com.complyt.domain.*;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusOrderAmountExtractorTest {

    @InjectMocks
    NexusOrderAmountExtractor nexusOrderAmountExtractor;

    @Mock
    ItemsCheck itemsCheck;

    Order order;
    NexusStateRule nexusStateRule;
    Customer customer;
    ObjectId customerId;

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        order = createOrder();
        nexusStateRule = createNexusStateRule();
    }

    private Customer createCustomer() {
        customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, clientId, CustomerType.RETAIL);
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

    private Order createOrder() {
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

        return new Order(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, OrderStatus.ACTIVE, clientId, null, new TimeStamps(new Date(), new Date()));
    }

    @Test
    void extract_ExtractsOrderItemsAmount_ReturnsAmount() {
        // Given

        // When
        when(itemsCheck.isCounted(order.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(itemsCheck.isCounted(order.getItems().get(1), nexusStateRule)).thenReturn(false);
        float amount = nexusOrderAmountExtractor.extract(order, nexusStateRule);

        // Then
        assertEquals(amount,order.getItems().get(0).getTotalPrice());
    }

    @Test
    void extract_NullOrderPassed_ThrowsException() {
        // Given
        Order nullOrder = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusOrderAmountExtractor.extract(nullOrder,nexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void extract_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusOrderAmountExtractor.extract(order,nullNexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

}
