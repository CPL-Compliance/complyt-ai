package com.complyt.config;

import com.complyt.domain.sales_tax.mappers.FastTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MappersConfigTest {

    @InjectMocks
    MappersConfig mappersConfig;

    @Test
    void fastTaxDataToSalesTaxRateMapper_CreatesMapperInstance_ReturnsMapperInstance(){
        // Given
        FastTaxDataToSalesTaxRateMapper fastTaxDataToSalesTaxRateMapper = FastTaxDataToSalesTaxRateMapper.INSTANCE;

        // When
        FastTaxDataToSalesTaxRateMapper secondFastTaxDataToSalesTaxRateMapper = mappersConfig.fastTaxDataToSalesTaxRateMapper();

        // Then
        Assertions.assertEquals(fastTaxDataToSalesTaxRateMapper, secondFastTaxDataToSalesTaxRateMapper);
    }
}
