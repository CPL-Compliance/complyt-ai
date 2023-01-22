package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Item;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemsSalesTaxRatesCalculatorTest {

    @InjectMocks
    ItemsSalesTaxRatesCalculator itemsSalesTaxRatesCalculator;

    @Mock
    SalesTaxRatesProvider salesTaxRateCalculator;
    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SalesTaxRate salesTaxRate;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = domainObjectStub.createJurisdictionalSalesTaxRules();
        salesTaxRate = domainObjectStub.createSalesTaxRates();
    }

    private List<Item> createItems() {
        return new ArrayList<>() {{
            add(new Item(1000, 2, 2000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(3000, 3, 9000, "description", "name", "taxCode", jurisdictionalSalesTaxRules, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
    }

    private List<Item> setRatesToItems(List<Item> items) {
        return items.stream().map(item -> item.withSalesTaxRate(salesTaxRate)).collect(Collectors.toList());
    }

    @Test
    void setSalesTaxRates_SetsRatesToItems_ReturnsModifiedItems() {
        // Given
        List<Item> items = createItems();
        List<Item> itemsWithRates = setRatesToItems(items);

        // When
        when(salesTaxRateCalculator.calculateSalesTaxRate(jurisdictionalSalesTaxRules, salesTaxRate)).thenReturn(salesTaxRate);
        List<Item> actualItems = itemsSalesTaxRatesCalculator.setSalesTaxRates(items, salesTaxRate);

        // Then
        assertEquals(itemsWithRates, actualItems);
    }
}
