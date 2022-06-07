package com.complyt.services;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.tax_reliefs.JurisdictionalSalesTaxController;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxServiceImplTest {

    @InjectMocks
    SalesTaxServiceImpl salesTaxService;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;
    @Mock
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;
    @Mock
    SalesTaxCalculator salesTaxCalculator;
    @Mock
    JurisdictionalSalesTaxController jurisdictionalSalesTaxController;

    @BeforeEach
    void setUp(){

    }

    @Test
    void initService_AllFieldsAreValid_InstantiatingService(){
        // Given
        SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator,jurisdictionalSalesTaxController);

        // Then
        Assertions.assertNotNull(salesTaxServiceImpl);
    }

    @Test
    void initService_NullClientWrapper_ThrowsException(){
        // Given
        salesTaxWebClientWrapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator,jurisdictionalSalesTaxController);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxWebClientWrapper is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxMapper_ThrowsException(){
        // Given
        salesTaxDataToSalesTaxRateMapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator,jurisdictionalSalesTaxController);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxDataToSalesTaxRateMapper is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxCalculator_ThrowsException(){
        // Given
        salesTaxCalculator = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator,jurisdictionalSalesTaxController);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxCalculator is marked non-null but is null");
    }

    @Test
    void initService_NullJurisdictionalSalesTaxController_ThrowsException(){
        // Given
        jurisdictionalSalesTaxController = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator,jurisdictionalSalesTaxController);
        });

        assertEquals(nullPointerException.getMessage(), "jurisdictionalSalesTaxController is marked non-null but is null");
    }
    
}
