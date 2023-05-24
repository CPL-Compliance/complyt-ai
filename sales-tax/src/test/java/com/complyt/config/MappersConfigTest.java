package com.complyt.config;

import com.complyt.domain.sales_tax.mappers.ComplytSalesTaxRatesToSalesTaxRatesMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MappersConfigTest {

    @InjectMocks
    MappersConfig mappersConfig;

    @Test
    void complytSalesTaxRatesToSalesTaxRatesMapper_CreatesFastTaxDataToSalesTaxRateMapper_ReturnFastTaxDataToSalesTaxRateMapper() {
        // Given
        ComplytSalesTaxRatesToSalesTaxRatesMapper complytSalesTaxRatesToSalesTaxRatesMapper = ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE;

        // When
        ComplytSalesTaxRatesToSalesTaxRatesMapper secondComplytSalesTaxRatesToSalesTaxRatesMapper = mappersConfig.complytSalesTaxRatesToSalesTaxRatesMapper();

        // Then
        Assertions.assertEquals(complytSalesTaxRatesToSalesTaxRatesMapper, secondComplytSalesTaxRatesToSalesTaxRatesMapper);
    }

}
