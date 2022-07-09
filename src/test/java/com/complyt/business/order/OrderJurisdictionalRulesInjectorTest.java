package com.complyt.business.order;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.nexus.TangibleCategory;
import com.complyt.domain.nexus.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderJurisdictionalRulesInjectorTest {

    Order order;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0, TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };

        order = Order.builder()
                .id(id)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .clientId(customerId)
                .orderStatus(OrderStatus.ACTIVE)
                .clientId(clientId)
                .build();
    }

    @Test
    void testAct() {
        Item item1NoRule = new Item(2000, 4, 8000, "description", "name", "C1S1",
                null,null,false,0,TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE);
        Item item2NoRule = new Item(2000, 4, 8000, "description", "name", "C2S2",
                null,null,false,0,TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE);
        List<Item> itemsNoRules = new ArrayList<Item>(){{
            add(item1NoRule);
            add(item2NoRule);
        }};
        JurisdictionalSalesTaxRules rule1 = new JurisdictionalSalesTaxRules("rule1","CA",true,false,
                CalculationType.FIXED,"rule1",0,null);
        JurisdictionalSalesTaxRules rule2 = new JurisdictionalSalesTaxRules("rule2","CA",true,false,
                CalculationType.FIXED,"rule2",0,null);

        Map<String,JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRules1 = new HashMap<String,JurisdictionalSalesTaxRules>(){{
            put(rule1.getAbbreviation(),rule1);
        }};
        Map<String,JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRules2 = new HashMap<String,JurisdictionalSalesTaxRules>(){{
            put(rule2.getAbbreviation(),rule2);
        }};
        ProductClassification productClassification1 = new ProductClassification("id","C1S1","description","title",jurisdictionalSalesTaxRules1);
        ProductClassification productClassification2 = new ProductClassification("id","C2S2","description","title",jurisdictionalSalesTaxRules2);

        Map<String,ProductClassification> productClassifications = new HashMap<String,ProductClassification>(){{
            put(productClassification1.getTaxCode(),productClassification1);
            put(productClassification2.getTaxCode(),productClassification2);
        }};

        Item item1WithRule = item1NoRule.withJurisdictionalSalesTaxRules(rule1);
        Item item2WithRule = item2NoRule.withJurisdictionalSalesTaxRules(rule2);
        List<Item> itemsWithRules = new ArrayList<Item>(){{
            add(item1WithRule);
            add(item2WithRule);
        }};

        Order order2 = order.withItems(itemsNoRules);
        OrderJurisdictionalRulesInjector orderProductClassificationInjector2 = new OrderJurisdictionalRulesInjector(order2);

        Order newOrder = order.withItems(itemsWithRules);

        Mono<Order> orderMono = orderProductClassificationInjector2.act(productClassifications);

        StepVerifier.create(orderMono).expectNext(newOrder).verifyComplete();

    }

}
