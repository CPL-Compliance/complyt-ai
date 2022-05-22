package com.complyt.services;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax.SalesTaxCalculator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @BeforeEach
    void setUp(){

    }

    @Test
    void initService_AllFieldsAreValid_InstantiatingService(){
        // Given
        SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator);

        // Then
        Assertions.assertNotNull(salesTaxServiceImpl);
    }

    @Test
    void initService_NullClientWrapper_ThrowsException(){
        // Given
        salesTaxWebClientWrapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxWebClientWrapper is marked non-null but is null");

    }

    @Test
    void initService_NullSalesTaxMapper_ThrowsException(){
        // Given
        salesTaxDataToSalesTaxRateMapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxDataToSalesTaxRateMapper is marked non-null but is null");

    }

    @Test
    void initService_NullSalesTaxCalculator_ThrowsException(){
        // Given
        salesTaxCalculator = null;
        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxServiceImpl salesTaxServiceImpl = new SalesTaxServiceImpl(salesTaxWebClientWrapper,salesTaxDataToSalesTaxRateMapper,salesTaxCalculator);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxCalculator is marked non-null but is null");
    }


}
