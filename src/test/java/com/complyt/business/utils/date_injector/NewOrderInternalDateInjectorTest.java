package com.complyt.business.utils.date_injector;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewOrderInternalDateInjectorTest {

    NewOrderInternalDateInjector newOrderInternalDateInjector;

    @BeforeEach
    void setUp() {
        Order orderWithOutInternalTimeStamps = createOrder();
        newOrderInternalDateInjector = new NewOrderInternalDateInjector(orderWithOutInternalTimeStamps);
    }

    private Order createOrder() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f);
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode",null,salesTaxRate,false,0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        return new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, new ObjectId(),  null,null);
    }


    @Test
    void inject_InjectsNewTimeStamps_ReturnsOrder() {
        // Given
        Date now = new Date();
        LocalDateTime localDateTime = LocalDateTime
                .ofInstant(now.toInstant(), ZoneId.systemDefault());

        // When + Then
        Order orderWithTimeStamps = newOrderInternalDateInjector.inject();
        TimeStamps actualTimeStamps = orderWithTimeStamps.getInternalTimeStamps();

        LocalDateTime localCreated = LocalDateTime
                .ofInstant(actualTimeStamps.getCreatedDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime localUpdated = LocalDateTime
                .ofInstant(actualTimeStamps.getUpdatedDate().toInstant(), ZoneId.systemDefault());


        assertEquals(localCreated.getYear(),localDateTime.getYear());
        assertEquals(localCreated.getMonthValue(),localDateTime.getMonthValue());
        assertEquals(localCreated.getDayOfMonth(),localDateTime.getDayOfMonth());

        assertEquals(localUpdated.getYear(),localDateTime.getYear());
        assertEquals(localUpdated.getMonthValue(),localDateTime.getMonthValue());
        assertEquals(localUpdated.getDayOfMonth(),localDateTime.getDayOfMonth());
    }
}
