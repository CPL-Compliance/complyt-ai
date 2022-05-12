package com.complyt.facades;

import com.complyt.business.sales_tax.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.SalesTaxWebClientWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SalesTaxFacadeTest {

    @InjectMocks
    SalesTaxFacade salesTaxFacade;

    @Mock
    FastTaxWebClientWrapper fastTaxWebClientWrapper;

    @Test
    void getSalesTax() {
    }

    @Test
    void initSalesTaxFacade_ValidSalesTaxWebClientWrapperGiven_InstantiatingSalesTaxFacade(){
        // Given
        SalesTaxFacade salesTaxFacade = new SalesTaxFacade(fastTaxWebClientWrapper);

        // Then
        assertNotNull(salesTaxFacade);
    }

    @Test
    void initSalesTaxFacade_NullSalesTaxWebClientWrapperGiven_ThrowsException(){
        // Given
        SalesTaxWebClientWrapper salesTaxWebClientWrapper = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            SalesTaxFacade salesTaxFacade = new SalesTaxFacade(salesTaxWebClientWrapper);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxWebClientWrapper is marked non-null but is null");
    }

    @Test
    void findByAddress_FindsByAddress_ReturnsSalesTaxData(){
        // Given

        // When
//        when(fastTaxWebClientWrapper.findByAddress())
        // Then

    }


}