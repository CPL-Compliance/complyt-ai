package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ItemsSalesTaxRatesProviderTest {

    @InjectMocks
    ItemsSalesTaxRatesProvider itemsSalesTaxRatesProvider;
    @Mock
    SalesTaxRatesProvider salesTaxRateCalculator;
    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;
    SalesTaxRates salesTaxRates;

    UnitTestUtilities testUtilities;
    Address address;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        salesTaxRates = testUtilities.createSalesTaxRates();
        address = testUtilities.createAddress();
    }

    private List<Item> createItems() {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(1000), new BigDecimal(2), new BigDecimal(2000), "description", "name", "taxCode", jurisdictionalSalesTaxRules, null, false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(3000),new BigDecimal( 3),new BigDecimal( 9000), "description", "name", "taxCode", jurisdictionalSalesTaxRules, null, false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
    }

    private List<Item> setRatesToItems(List<Item> items) {
        return items.stream().map(item -> item.withSalesTaxRates(salesTaxRates)).collect(Collectors.toList());
    }

    @Test
    void setSalesTaxRates_SetsRatesToItems_ReturnsModifiedItems() {
        // Given
        List<Item> items = createItems();
        List<Item> itemsWithRates = setRatesToItems(items);

        // When
        when(salesTaxRateCalculator.provide(jurisdictionalSalesTaxRules, salesTaxRates, address)).thenReturn(salesTaxRates);
        List<Item> actualItems = itemsSalesTaxRatesProvider.setSalesTaxRates(items, salesTaxRates, address);

        // Then
        assertEquals(itemsWithRates, actualItems);
    }
}
