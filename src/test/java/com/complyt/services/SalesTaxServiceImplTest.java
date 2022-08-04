package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.county_injector.CountyInjector;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
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

    @Mock
    SalesTaxApplyCheck salesTaxApplyCheck;

    @Mock
    CountyInjector countyInjector;

    Order order;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        ObjectId clientId = new ObjectId();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId, null, externalTimeStamps);
    }

    @Test
    void initService_NullClientWrapper_ThrowsException() {
        // Given
        salesTaxWebClientWrapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, salesTaxCalculator, salesTaxRateCalculator, salesTaxApplyCheck,null);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxWebClientWrapper is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxMapper_ThrowsException() {
        // Given
        salesTaxDataToSalesTaxRate = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, salesTaxCalculator, salesTaxRateCalculator, salesTaxApplyCheck,null);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxDataToSalesTaxRate is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxCalculator_ThrowsException() {
        // Given
        salesTaxCalculator = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, salesTaxCalculator, salesTaxRateCalculator, salesTaxApplyCheck,null);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxCalculator is marked non-null but is null");
    }

    @Test
    void initService_NullJurisdictionalSalesTaxController_ThrowsException() {
        // Given
        salesTaxRateCalculator = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, salesTaxCalculator, salesTaxRateCalculator, salesTaxApplyCheck,null);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxRateCalculator is marked non-null but is null");
    }

    @Test
    void handleSalesTaxCalculation_StateDoesntEnforceNexus_ReturnsSameOrder() {
        // Given
        State state = new State("CA", "02", "California");
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), false, null, null, LocalDateTime.now());

        // When
        Mono<Order> orderMono = salesTaxService.handleSalesTaxCalculation(order, tracking);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NexusIsNotAppliedYet_ReturnsSameOrder() {
        // Given
        State state = new State("CA", "02", "California");
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), false, null, null, LocalDateTime.now().plusYears(1));

        // When
        Mono<Order> orderMono = salesTaxService.handleSalesTaxCalculation(order, tracking);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_SalesTaxCalculated_OrderModified() {
        // Given
        FastTaxData fastTaxData = new FastTaxData();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        SalesTax salesTax = new SalesTax(10, salesTaxRate);

        List<Item> itemsWithRates = new ArrayList<Item>() {{
            add(order.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Order orderWithCounty = order.withShippingAddress(order.getShippingAddress().withCounty("county"));
        Order orderWithSalesTax = orderWithCounty.withItems(itemsWithRates).withSalesTax(salesTax).withShippingAddress(order.getShippingAddress().withCounty("county"));

        State state = new State("CA", "02", "California");
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), true, null, null, LocalDateTime.now().minusYears(1));


        // When
        when(salesTaxApplyCheck.isApplied(order, tracking)).thenReturn(true);
        when(countyInjector.inject(order,fastTaxData)).thenReturn(orderWithCounty);
        when(salesTaxWebClientWrapper.findByAddress(order.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(salesTaxRate);
        when(salesTaxRateCalculator.calculateSalesTaxRate(order.getItems().get(0).getJurisdictionalSalesTaxRules(), salesTaxRate))
                .thenReturn(salesTaxRate);
        when(salesTaxCalculator.calculate(itemsWithRates)).thenReturn(salesTax.getAmount());
        Mono<Order> orderMono = salesTaxService.handleSalesTaxCalculation(order, tracking);

        // Then
        StepVerifier.create(orderMono).expectNext(orderWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NullOrderPassed_ThrowsException() {
        // Given
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), null,
                new ObjectId(), true, null, null, null);
        Order nullOrder = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxService.handleSalesTaxCalculation(nullOrder, tracking);
        });
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");

    }

    @Test
    void calculate_NullOrderPassed_ThrowsException() {
        // Given
        Order nullOrder = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxService.injectCountyToOrderAndCalculate(nullOrder);
        });
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");

    }

    @Test
    void handleSalesTaxCalculation_NullTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullTracking = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxService.handleSalesTaxCalculation(order, nullTracking);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }
}
