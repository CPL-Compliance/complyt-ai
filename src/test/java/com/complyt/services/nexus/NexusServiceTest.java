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
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
                TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR, nexusThreshold);
    }

    private Order createOrder() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
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

    @Test
    void getClientTracking() {
    }

    @Test
    void findTrackingByState() {
    }

    @Test
    void findRuleByState() {
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