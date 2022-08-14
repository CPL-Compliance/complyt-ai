package com.complyt.business.sales_tax.tax_reliefs;

import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JurisdictionalSalesTaxControllerTest {

    private SalesTaxRateCalculator jurisdictionalSalesTaxController;

    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    private SalesTaxRate salesTaxRateByService;

    @BeforeEach
    void set_up(){
        salesTaxRateByService = new SalesTaxRate(0.05f,0.05f,0.05f,0.05f,0.05f,0.25f);
        jurisdictionalSalesTaxController = new SalesTaxRateCalculator();
        jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules(
                "California","CA", true,true, CalculationType.FIXED,
                "description",0.07f,null);
    }

    @Test
    void getRateByRules_NotTaxable_ReturnsZeroRate(){
        // Given
        SalesTaxRate zeroSalesTaxRate = new SalesTaxRate(0,0,0,0,0,0);
        JurisdictionalSalesTaxRules notTaxableRule = jurisdictionalSalesTaxRules.withTaxable(false);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.calculateSalesTaxRate(notTaxableRule, salesTaxRateByService);
        Assertions.assertEquals(returnedRate, zeroSalesTaxRate);
    }

    @Test
    void getRateByRules_NoSpecialTreatment_ReturnsRateGivenBySalesTaxEngine(){
        // Given
        JurisdictionalSalesTaxRules noSpecialTreatmentRule = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.calculateSalesTaxRate(noSpecialTreatmentRule, salesTaxRateByService);
        Assertions.assertEquals(returnedRate, salesTaxRateByService);
    }

    @Test
    void getRateByRules_CalculationTypeSetToFixed_OverridesStateRate(){
        // Given
        float newStateRate = jurisdictionalSalesTaxRules.getCalculationValue();
        float newTaxRate = salesTaxRateByService.getTaxRate() - salesTaxRateByService.getStateRate() + newStateRate;
        SalesTaxRate expectedSalesTaxRate = salesTaxRateByService.withTaxRate(newTaxRate).withStateRate(newStateRate);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.calculateSalesTaxRate(jurisdictionalSalesTaxRules, salesTaxRateByService);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }

    @Test
    void getRateByRules_CalculationTypeSetToPercentage_OverridesStateRate(){
        // Given
        float newStateRate = salesTaxRateByService.getStateRate() * jurisdictionalSalesTaxRules.getCalculationValue();
        JurisdictionalSalesTaxRules percentageCalculationTypeRule = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.PERCENTAGE);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.calculateSalesTaxRate(percentageCalculationTypeRule, salesTaxRateByService);
        Assertions.assertEquals(salesTaxRateByService, returnedRate);
    }
}
