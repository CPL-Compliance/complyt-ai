package io.complyt.domain.sales_tax;

import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.sales_tax.FilingMetaData;
import io.complyt.domain.transaction.MatchedAddressData;
import io.complyt.domain.transaction.MandatoryAddress;
import io.complyt.domain.transaction.Scoring;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ComplytSalesTaxRatesTest {

    @Test
    void testComplytSalesTaxRatesCreationAndGetters() {
        UUID complytId = UUID.randomUUID();

        MandatoryAddress address = new MandatoryAddress("City", "USA", "County", "State", "Street", "Region", "Zip", false);
        Scoring scoring = new Scoring(MatchLevelType.EXCELLENT, 0.95,
                new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
        MatchedAddressData matchedAddressData = new MatchedAddressData(address, scoring);

        SalesTaxRates salesTaxRates = SalesTaxRates.zeroSalesTaxRate();

        FilingMetaData filingMetaData = new FilingMetaData(
                "city", "county",
                BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO,
                "CRPT", "CTY", "MTA", "MTA123",
                "SPD", "SPD456", "Other1", "O1", "Other2", "O2",
                "Other3", "O3", "Other4", "O4", "FIPS"
        );

        ComplytSalesTaxRates rates = new ComplytSalesTaxRates(complytId, matchedAddressData, salesTaxRates, filingMetaData);

        assertEquals(complytId, rates.complytId());
        assertEquals(matchedAddressData, rates.matchedAddressData());
        assertEquals(salesTaxRates, rates.salesTaxRates());
        assertEquals(filingMetaData, rates.filingMetaData());
    }

    @Test
    void testWithMethodCreatesCopy() {
        ComplytSalesTaxRates original = new ComplytSalesTaxRates(
                UUID.randomUUID(),
                null,
                SalesTaxRates.zeroSalesTaxRate(),
                null
        );

        UUID newId = UUID.randomUUID();
        ComplytSalesTaxRates updated = original.withComplytId(newId);

        assertEquals(newId, updated.complytId());
        assertNotEquals(original.complytId(), updated.complytId());
        assertEquals(original.salesTaxRates(), updated.salesTaxRates());
    }
}
