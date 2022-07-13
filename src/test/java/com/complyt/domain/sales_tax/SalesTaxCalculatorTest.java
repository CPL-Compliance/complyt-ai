package com.complyt.domain.sales_tax;

import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.domain.Item;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxCalculatorTest {

    @InjectMocks
    SalesTaxCalculator salesTaxCalculator;

    JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules;

    @BeforeEach
    void setUp() {
        jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California","CA",true,true,
                CalculationType.FIXED,"description",0.5f,null);
    }

    @Test
    void calculate_SalesTaxBeingCalculated_SalesTaxAmountReturned(){
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f);

        List<Item> items = new ArrayList<Item>(){{
                add(new Item(1000,2,2000,"description","name","taxCode",jurisdictionalSalesTaxRules, salesTaxRate,false,0, TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE));
                add(new Item(3000,3,9000,"description","name","taxCode",jurisdictionalSalesTaxRules, salesTaxRate,false,0,TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = 0;

        // When
        for(Item item : items){
            amount += salesTaxRate.getTaxRate() * item.getTotalPrice();
        }

        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items);

        // Then
        assertEquals(amount,salesTaxAmountReturnedFromCalculation);

    }

    @Test
    void calculate_OrderComesWithItemWithManualSalesTax_SalesTaxAmountReturned(){
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f);
        List<Item> items = new ArrayList<Item>(){{
            add(new Item(1000,2,2000,"description","name","taxCode",null, salesTaxRate,true,0.5f,TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float amount = items.get(0).getTotalPrice() * 0.5f;

        // When
        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items);

        // Then
        assertEquals(amount,salesTaxAmountReturnedFromCalculation);

    }

    @Test
    void calculate_JurisdictionalSalesTaxRulesIsCalculatedByPercentage_SalesTaxAmountReturned(){
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f);
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRulesByPercentage = jurisdictionalSalesTaxRules.withCalculationType(CalculationType.PERCENTAGE);
        List<Item> items = new ArrayList<Item>(){{
            add(new Item(1000,2,2000,"description","name","taxCode",
                    jurisdictionalSalesTaxRulesByPercentage, salesTaxRate,false,0.5f,TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE));
        }};
        float partialTotalPrice = items.get(0).getTotalPrice() * jurisdictionalSalesTaxRulesByPercentage.getCalculationValue();
        float amount = partialTotalPrice * items.get(0).getSalesTaxRate().getTaxRate();

        // When
        float salesTaxAmountReturnedFromCalculation = salesTaxCalculator.calculate(items);

        // Then
        assertEquals(amount,salesTaxAmountReturnedFromCalculation);
    }

}
