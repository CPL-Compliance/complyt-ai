package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.tax.gt.GtRatesProvider;
import com.complyt.business.tax.gt.ItemsGtRatesProvider;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemsGtRatesProviderTest {

    @InjectMocks
    ItemsGtRatesProvider itemsGstRatesProvider;
    @Mock
    GtRatesProvider gtRatesProvider;
    UnitTestUtilities testUtilities;
    List<Item> items;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        items = testUtilities.createItems(false, true, true);
    }

    @Test
    void setGstRates_SetsGstRatesInToItems_ReturnsItems() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        GtAddress gtAddress = testUtilities.createCanadaGtAddress();
        List<Item> expectedItems = new ArrayList<>() {{
            add(items.get(0).withGtRates(gtRates));
            add(items.get(1).withGtRates(gtRates));
        }};

        // When
        when(gtRatesProvider.provide(items.get(0).getJurisdictionalTaxRules(), gtRates, gtAddress)).thenReturn(gtRates);
        when(gtRatesProvider.provide(items.get(1).getJurisdictionalTaxRules(), gtRates, gtAddress)).thenReturn(gtRates);
        List<Item> actualItems = itemsGstRatesProvider.setGtRates(items, gtRates, gtAddress);

        // Then
        Assertions.assertEquals(expectedItems, actualItems);
    }

}