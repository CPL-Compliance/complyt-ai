package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class SalesTaxServiceImplTest {

    @InjectMocks
    SalesTaxServiceImpl salesTaxService;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Mock
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRate;

    @Mock
    SalesTaxCalculator salesTaxCalculator;

    @Mock
    SalesTaxRateCalculator salesTaxRateCalculator;

    Order order;

    @BeforeEach
    void setUp(){
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        ObjectId clientId = new ObjectId();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null,false,0
        ));
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId);
    }

    @Test
    void initService_AllFieldsAreValid_InstantiatingService(){
        // Given
        SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate,salesTaxCalculator, salesTaxRateCalculator);

        // Then
        Assertions.assertNotNull(salesTaxServiceImpl);
    }

    @Test
    void initService_NullClientWrapper_ThrowsException(){
        // Given
        salesTaxWebClientWrapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate,salesTaxCalculator, salesTaxRateCalculator);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxWebClientWrapper is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxMapper_ThrowsException(){
        // Given
        salesTaxDataToSalesTaxRate = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate,salesTaxCalculator, salesTaxRateCalculator);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxDataToSalesTaxRate is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxCalculator_ThrowsException(){
        // Given
        salesTaxCalculator = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate,salesTaxCalculator, salesTaxRateCalculator);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxCalculator is marked non-null but is null");
    }

    @Test
    void initService_NullJurisdictionalSalesTaxController_ThrowsException(){
        // Given
        salesTaxRateCalculator = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate,salesTaxCalculator, salesTaxRateCalculator);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxRateCalculator is marked non-null but is null");
    }

    @Test
    void calculate_SalesTaxCalculated_OrderModified() {
        // Given
        FastTaxData fastTaxData = new FastTaxData();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        SalesTax salesTax = new SalesTax(10,salesTaxRate);

        List<Item> itemsWithRates = new ArrayList<Item>(){{
            add(order.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Order orderWithSalesTax = order.withItems(itemsWithRates).withSalesTax(salesTax);

        // When
        when(salesTaxWebClientWrapper.findByAddress(order.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(salesTaxRate);
        when(salesTaxRateCalculator.calculateSalesTaxRate(order.getItems().get(0).getJurisdictionalSalesTaxRules(), salesTaxRate))
                .thenReturn(salesTaxRate);
        when(salesTaxCalculator.calculate(itemsWithRates)).thenReturn(salesTax.getAmount());
        Mono<Order> orderMono = salesTaxService.calculate(order);

        // Then
        StepVerifier.create(orderMono).expectNext(orderWithSalesTax).verifyComplete();
    }

}
