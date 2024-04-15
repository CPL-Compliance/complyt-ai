package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.transaction.ShippingFee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ShippingFeeQualificationCheckerTest {

    private UnitTestUtilities testUtilities;
    private QualificationChecker qualificationChecker;
    private NexusStateRule nexusStateRule;
    private ShippingFee shippingFee;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        qualificationChecker = new QualificationChecker();
        nexusStateRule = createNexusStateRule();
        shippingFee = testUtilities.createShippingFee(true, false, true)
                .withTangibleCategory(TangibleCategory.TANGIBLE);
    }

//    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
//        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
//                CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
//    }

    private NexusStateRule createNexusStateRule() {
        String country = "USA";
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, country, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR, nexusThreshold, LocalDateTime.now());
    }

    @Test
    void isQualified_ShippingFeeQualifies_ReturnsTrue() {
        // Given

        // When + Then
        boolean isQualified = qualificationChecker.isQualified(shippingFee, nexusStateRule);
        assertTrue(isQualified);
    }


    @Test
    void isQualified_NotQualifiedBecauseOfTaxableCategory_ReturnsFalse() {
        // Given
        ShippingFee notTaxableShippingFee = shippingFee.withTaxableCategory(TaxableCategory.NOT_TAXABLE);

        // When + Then
        boolean isQualified = qualificationChecker.isQualified(notTaxableShippingFee, nexusStateRule);
        assertFalse(isQualified);
    }

    @Test
    void isQualified_NotQualifiedBecauseOfTangibleCategory_ReturnsFalse() {
        // Given
        ShippingFee notTangibleShippingFee = shippingFee.withTangibleCategory(TangibleCategory.INTANGIBLE);

        // When + Then
        boolean isQualified = qualificationChecker.isQualified(notTangibleShippingFee, nexusStateRule);
        assertFalse(isQualified);
    }

    @Test
    void isQualified_nullShippingFeePassed_ReturnsFalse() {
        // Given
        ShippingFee nullShippingFee = null;

        // When + Then
        boolean isQualified = qualificationChecker.isQualified(nullShippingFee, nexusStateRule);
        assertFalse(isQualified);
    }

    @Test
    void isQualified_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            qualificationChecker.isQualified(shippingFee, nullStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

}
