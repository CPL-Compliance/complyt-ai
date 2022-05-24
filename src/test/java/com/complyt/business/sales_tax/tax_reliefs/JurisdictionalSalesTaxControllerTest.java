package com.complyt.business.sales_tax.tax_reliefs;

import com.complyt.business.tax_reliefs.JurisdictionalSalesTaxController;
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

    private JurisdictionalSalesTaxController jurisdictionalSalesTaxController;

    private JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    private SalesTaxRate salesTaxRateByService;

    @BeforeEach
    void set_up(){
        salesTaxRateByService = new SalesTaxRate(0.05f,0.05f,0.05f,0.05f,0.05f,0.25f);
        jurisdictionalSalesTaxController = new JurisdictionalSalesTaxController();
        jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules(null,
                "California","CA", true,true, CalculationType.FIXED,
                "description",0.07f);
    }

    @Test
    void getRateByRules_NotTaxable_ReturnsZeroRate(){
        // Given
        SalesTaxRate zeroSalesTaxRate = new SalesTaxRate(0,0,0,0,0,0);
        JurisdictionalSalesTaxRules notTaxableRule = jurisdictionalSalesTaxRules.withTaxable(false);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.getRateByRules(notTaxableRule, salesTaxRateByService,null);
        Assertions.assertEquals(returnedRate, zeroSalesTaxRate);
    }

    @Test
    void getRateByRules_NoSpecialTreatment_ReturnsRateGivenBySalesTaxEngine(){
        // Given
        JurisdictionalSalesTaxRules noSpecialTreatmentRule = jurisdictionalSalesTaxRules.withSpecialTreatment(false);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.getRateByRules(noSpecialTreatmentRule, salesTaxRateByService,null);
        Assertions.assertEquals(returnedRate, salesTaxRateByService);
    }

    @Test
    void getRateByRules_CalculationTypeSetToFixed_OverridesStateRate(){
        // Given
        float newStateRate = jurisdictionalSalesTaxRules.getCalculationValue();
        float newTaxRate = salesTaxRateByService.getTaxRate() - salesTaxRateByService.getStateRate() + newStateRate;
        SalesTaxRate expectedSalesTaxRate = salesTaxRateByService.withTaxRate(newTaxRate).withStateRate(newStateRate);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.getRateByRules(jurisdictionalSalesTaxRules, salesTaxRateByService,null);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }

    @Test
    void getRateByRules_CalculationTypeSetToPercentage_OverridesStateRate(){
        // Given
        float newStateRate = salesTaxRateByService.getStateRate() * jurisdictionalSalesTaxRules.getCalculationValue();
        float newTaxRate = salesTaxRateByService.getTaxRate() - salesTaxRateByService.getStateRate() + newStateRate;
        SalesTaxRate expectedSalesTaxRate = salesTaxRateByService.withTaxRate(newTaxRate).withStateRate(newStateRate);
        JurisdictionalSalesTaxRules precentageCalculationTypeRule = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.PERCENTAGE);

        // When + Then
        SalesTaxRate returnedRate = jurisdictionalSalesTaxController.getRateByRules(precentageCalculationTypeRule, salesTaxRateByService,null);
        Assertions.assertEquals(expectedSalesTaxRate, returnedRate);
    }
}
