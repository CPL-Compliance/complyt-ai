package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.domain.Item;
import com.complyt.domain.State;
import com.complyt.domain.Taxable;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TaxableAmountExtractorTest {

    TaxableAmountExtractor taxableAmountExtractor;

    @Mock
    QualificationCheck qualificationCheck;

    Taxable taxable;

    NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        nexusStateRule = createNexusStateRule();
        taxable = createTaxable();
        taxableAmountExtractor = new TaxableAmountExtractor(qualificationCheck, taxable, nexusStateRule);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    private Taxable createTaxable() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        JurisdictionalSalesTaxRules rule = createJurisdictionalSalesTaxRules();
        return new Item(2000, 4, 8000, "description", "name", "taxCode", rule, salesTaxRate, false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE);
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
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    @Test
    void extract_TaxableItemIsQualified_ReturnsItemTotalPrice() {
        // Given
        float expectedAmount = taxable.getTotalPrice();

        // When
        when(qualificationCheck.isQualified(taxable,nexusStateRule)).thenReturn(true);
        float actualAmount = taxableAmountExtractor.extract();

        // Then
        Assertions.assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void extract_TaxableItemIsNotQualified_ReturnsZero() {
        // Given
        float expectedAmount = 0;

        // When
        when(qualificationCheck.isQualified(taxable,nexusStateRule)).thenReturn(false);
        float actualAmount = taxableAmountExtractor.extract();

        // Then
        Assertions.assertEquals(expectedAmount, actualAmount);
    }
}
