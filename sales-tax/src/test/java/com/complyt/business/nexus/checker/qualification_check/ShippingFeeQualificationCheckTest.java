package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShippingFeeQualificationCheckTest {

    QualificationCheck qualificationCheck;
    NexusStateRule nexusStateRule;
    ShippingFee shippingFee;

    @BeforeEach
    void setUp() {
        qualificationCheck = new QualificationCheck();
        nexusStateRule = createNexusStateRule();
        shippingFee = createShippingFee();
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.TANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    private NexusStateRule createNexusStateRule() {
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

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR, nexusThreshold);
    }

    @Test
    void isQualified_ShippingFeeQualifies_ReturnsTrue() {
        // Given

        // When + Then
        boolean isQualified = qualificationCheck.isQualified(shippingFee,nexusStateRule);
        assertTrue(isQualified);
    }


    @Test
    void isQualified_NotQualifiedBecauseOfTaxableCategory_ReturnsFalse() {
        // Given
        ShippingFee notTaxableShippingFee = shippingFee.withTaxableCategory(TaxableCategory.NOT_TAXABLE);

        // When + Then
        boolean isQualified = qualificationCheck.isQualified(notTaxableShippingFee,nexusStateRule);
        assertFalse(isQualified);
    }

    @Test
    void isQualified_NotQualifiedBecauseOfTangibleCategory_ReturnsFalse() {
        // Given
        ShippingFee notTangibleShippingFee = shippingFee.withTangibleCategory(TangibleCategory.INTANGIBLE);

        // When + Then
        boolean isQualified = qualificationCheck.isQualified(notTangibleShippingFee,nexusStateRule);
        assertFalse(isQualified);
    }

    @Test
    void isQualified_nullShippingFeePassed_ReturnsFalse() {
        // Given
        ShippingFee nullShippingFee = null;

        // When + Then
        boolean isQualified = qualificationCheck.isQualified(nullShippingFee,nexusStateRule);
        assertFalse(isQualified);
    }

    @Test
    void isQualified_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            qualificationCheck.isQualified(shippingFee, nullStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

}
