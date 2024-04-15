package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.business.tax.sales_tax.sales_tax_rates.ItemsSalesTaxRatesProvider;
import com.complyt.business.tax.sales_tax.sales_tax_rates.SalesTaxRatesProvider;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Item;
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
    List<Item> items;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        salesTaxRates = testUtilities.createSalesTaxRates();
        address = testUtilities.createAddress();
        items = testUtilities.createItems(true, false, false);
    } //note gt is null


    private List<Item> setRatesToItems(List<Item> items) {
        return items.stream().map(item -> item.withSalesTaxRates(salesTaxRates)).collect(Collectors.toList());
    }

    @Test
    void setSalesTaxRates_SetsRatesToItems_ReturnsModifiedItems() {
        // Given
        List<Item> itemList = items;
        List<Item> itemsWithRates = setRatesToItems(itemList);

        // When
        when(salesTaxRateCalculator.provide(jurisdictionalSalesTaxRules, salesTaxRates, address)).thenReturn(salesTaxRates);
        List<Item> actualItems = itemsSalesTaxRatesProvider.setSalesTaxRates(itemList, salesTaxRates, address);

        // Then
        assertEquals(itemsWithRates, actualItems);
    }
}
