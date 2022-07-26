package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.query.TimeFrameQueryBuilder;
import com.complyt.domain.*;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.OrderService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class NexusServiceTest {
    @InjectMocks
    NexusService nexusService;

    @Mock
    ClientTrackingService clientTrackingService;

    @Mock
    NexusStateRuleService nexusStateRuleService;

    @Mock
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    @Mock
    private OrderService orderService;

    @Mock
    private SalesTaxTrackingService salesTaxTrackingService;

    @Mock
    private NexusCalculator nexusCalculator;

    @Mock
    private NexusChecker nexusChecker;

    private Order order;
    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = createOrder();
        nexusStateRule = createNexusStateRule();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), true,
                new PhysicalNexusTracker(false, null),
                new EconomicNexusTracker(false, null));
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
            }
        };

        return new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId, null, new TimeStamps(new Date(), new Date()));
    }

    private SalesTaxTracking createSalesTaxTrackingWithoutNexusEstablished() {
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false,null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false,null);

        State state = new State("CA","02","California");
        return new SalesTaxTracking(UUID.randomUUID().toString(),state,new ObjectId(),
                true,physicalNexusTracker,economicNexusTracker);
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished() {
        SalesTaxTracking salesTaxTrackingWithNexus = createSalesTaxTrackingWithoutNexusEstablished()
                .withEconomicNexusTracker(new EconomicNexusTracker(true,new Date()));

        return salesTaxTrackingWithNexus;
    }

    @Test
    void calculate_NullOrderPassed_ThrowsException(){
        // Given
        Order nullOrder = null;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.calculateNexusTracking(nullOrder));

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void calculate_NexusDoesNotPassThreshold_NexusTrackingDoesNotChange() {
        // Given
        Nexus nexusInfo = new Nexus(false,null);
        NexusStateRule nexusStateRule = createNexusStateRule();
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(LocalDateTime.now().minusYears(1)).lte(LocalDateTime.now()));
        List<Order> ordersList = new ArrayList<Order>() {{add(order);}};
        Flux<Order> ordersFlux = Flux.fromIterable(ordersList);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount()-1,
                nexusStateRule.getNexusThreshold().getAmount()-1);

        State state = new State("CA","02","California");
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished();
        Date referenceDate = order.getExternalTimeStamps().getCreatedDate();

        // When
        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexusInfo));
        when(nexusStateRuleService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(nexusStateRule));
        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo,nexusStateRule,referenceDate)).thenReturn(query);
        when(orderService.getOrdersByQuery(query)).thenReturn(ordersFlux);
        when(nexusCalculator.calculate(ordersList,nexusStateRule)).thenReturn(summary);
        when(nexusChecker.passedThreshold(summary,nexusStateRule)).thenReturn(false);
        when(salesTaxTrackingService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.calculateNexusTracking(order);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void calculate_NexusPassesThreshold_NexusTrackingChanges() {
        // Given
        Nexus nexusInfo = new Nexus(false,null);
        NexusStateRule nexusStateRule = createNexusStateRule();
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(LocalDateTime.now().minusYears(1)).lte(LocalDateTime.now()));
        List<Order> ordersList = new ArrayList<Order>() {{add(order);}};
        Flux<Order> ordersFlux = Flux.fromIterable(ordersList);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount()+1,
                nexusStateRule.getNexusThreshold().getAmount()+1);

        State state = new State("CA","02","California");
        SalesTaxTracking salesTaxTrackingWithNoNexusEstablished = createSalesTaxTrackingWithoutNexusEstablished();
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = createSalesTaxTrackingWithNexusEstablished();
        Date referenceDate = order.getExternalTimeStamps().getCreatedDate();


        // When
        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexusInfo));
        when(nexusStateRuleService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(nexusStateRule));
        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo,nexusStateRule, referenceDate)).thenReturn(query);
        when(orderService.getOrdersByQuery(query)).thenReturn(ordersFlux);
        when(nexusCalculator.calculate(ordersList,nexusStateRule)).thenReturn(summary);
        when(nexusChecker.passedThreshold(summary,nexusStateRule)).thenReturn(true);
        when(salesTaxTrackingService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTrackingWithNoNexusEstablished));
        when(salesTaxTrackingService.saveWithEconomicQualified(salesTaxTrackingWithNoNexusEstablished))
                .thenReturn(Mono.just(salesTaxTrackingWithNexusEstablished));

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.calculateNexusTracking(order);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void getClientTracking() {
    }

    @Test
    void findTrackingByState_StateSent_FindsTracking_ReturnsTracking() {
        // Given
        SalesTaxTracking salesTaxTracking = createSalesTaxTracking();

        // When
        when(salesTaxTrackingService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.findTrackingByState(order.getShippingAddress().getState());

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findTrackingByState_OrderSent_FindsTracking_ReturnsTracking() {
        // Given
        SalesTaxTracking salesTaxTracking = createSalesTaxTracking();

        // When
        when(salesTaxTrackingService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.findTrackingByState(order);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findRuleByState_FindsRule_ReturnsRule() {
        // Given
        NexusStateRule nexusStateRule = createNexusStateRule();
        String state = nexusStateRule.getState().getAbbreviation();

        // When
        when(nexusStateRuleService.findByState(state)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> nexusStateRuleMono = nexusService.findRuleByState(state);

        // Then
        StepVerifier.create(nexusStateRuleMono).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void hasNexus_HasNexus_ReturnsHasNexus() {
        // Given
        SalesTaxTracking salesTaxTracking = createSalesTaxTracking();

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(true);
        boolean hasNexus = nexusService.hasNexus(salesTaxTracking);

        // Then
        Assertions.assertTrue(hasNexus);
    }

    @Test
    void hasNexus_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.hasNexus(nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void getOrdersByTimeFrame() {
    }

//
//    @Test
//    void calculate() {
//        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
//                .gte(LocalDateTime.now().minusYears(1).withMonth(1).withDayOfMonth(1)).lte(LocalDateTime.now()));
//
//        List<Order> filteredOrders = new ArrayList<Order>() {{
//            add(order);
//        }};
//
//        NexusCalculationSummary summary = new NexusCalculationSummary(1,order.getItems().get(0).getTotalPrice());
//        Nexus nexus = new Nexus(false, LocalDate.now());
//
//        NexusTracking nexusTracking = new NexusTracking(UUID.randomUUID().toString(),nexusStateRule.getState(),
//                new ObjectId(),true,new PhysicalNexusTracker(false,null),new EconomicNexusTracker(false,null));
//
//        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexus));
//        when(nexusStateRuleService.findByState(any())).thenReturn(Mono.just(nexusStateRule));
//        when(timeFrameQueryBuilder.buildNexusTimeFrame(nexus,nexusStateRule)).thenReturn(query);
//        when(orderService.getOrdersByFilter(query)).thenReturn(Flux.fromIterable(filteredOrders));
//        when(nexusCalculator.calculate(filteredOrders,nexusStateRule)).thenReturn(summary);
//        when(nexusChecker.passedThreshold(summary,nexusStateRule)).thenReturn(false);
//        when(nexusTrackingService.findByState(order.getShippingAddress().getState())).thenReturn(Mono.just(nexusTracking));
//
//        Mono<NexusTracking> nexusStateRuleMono = nexusService.handle(order);
//
//        StepVerifier.create(nexusStateRuleMono).expectNext(nexusTracking).verifyComplete();
//    }

    @Test
    void aggregateNexusInfo() {
    }
}