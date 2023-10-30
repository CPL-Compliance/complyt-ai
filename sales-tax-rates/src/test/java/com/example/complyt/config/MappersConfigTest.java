package com.example.complyt.config;

import com.complyt.config.MappersConfig;
import com.complyt.domain.mappers.FastTaxGetBestMatchDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.FastTaxGetByCityCountyStateDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.TaxJarDataToSalesTaxRateMapper;
import com.complyt.domain.mappers.ZipTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MappersConfigTest {

    @InjectMocks
    MappersConfig mappersConfig;

    @Test
    void fastTaxGetBestMatchDataToSalesTaxRateMapper_CreatesFastTaxGetBestMatchDataToSalesTaxRateMapper_ReturnFastTaxGetBestMatchDataToSalesTaxRateMapper() {
        // Given
        FastTaxGetBestMatchDataToSalesTaxRateMapper fastTaxGetBestMatchDataToSalesTaxRateMapper = FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE;

        // When
        FastTaxGetBestMatchDataToSalesTaxRateMapper secondfastTaxGetBestMatchDataGetBestMatchToSalesTaxRateMapper = mappersConfig.fastTaxGetBestMatchDataGetBestMatchToSalesTaxRateMapper();

        // Then
        Assertions.assertEquals(fastTaxGetBestMatchDataToSalesTaxRateMapper, secondfastTaxGetBestMatchDataGetBestMatchToSalesTaxRateMapper);
    }

    @Test
    void fastTaxGetByCityCountyStateDataToSalesTaxRateMapper_CreatesFastTaxGetByCityCountyStateDataToSalesTaxRateMapper_ReturnFastTaxGetByCityCountyStateDataToSalesTaxRateMapper() {
        // Given
        FastTaxGetByCityCountyStateDataToSalesTaxRateMapper fastTaxGetByCityCountyStateDataToSalesTaxRateMapper = FastTaxGetByCityCountyStateDataToSalesTaxRateMapper.INSTANCE;

        // When
        FastTaxGetByCityCountyStateDataToSalesTaxRateMapper secondFastTaxGetByCityCountyStateDataToSalesTaxRateMapper = mappersConfig.fastTaxGetByCityCountyDataToSalesTaxRateMapper();

        // Then
        Assertions.assertEquals(fastTaxGetByCityCountyStateDataToSalesTaxRateMapper, secondFastTaxGetByCityCountyStateDataToSalesTaxRateMapper);
    }


    @Test
    void zipTaxDataToSalesTaxRateMapper_CreatesZipTaxDataToSalesTaxRateMapper_ReturnZipTaxDataToSalesTaxRateMapper() {
        // Given
        ZipTaxDataToSalesTaxRateMapper zipTaxDataToSalesTaxRateMapper = ZipTaxDataToSalesTaxRateMapper.INSTANCE;

        // When
        ZipTaxDataToSalesTaxRateMapper secondZipTaxDataToSalesTaxRateMapper = mappersConfig.zipTaxDataToSalesTaxRateMapper();

        // Then
        Assertions.assertEquals(zipTaxDataToSalesTaxRateMapper, secondZipTaxDataToSalesTaxRateMapper);
    }

    @Test
    void taxJarDataToSalesTaxRateMapper_CreatesZipTaxDataToSalesTaxRateMapper_ReturnZipTaxDataToSalesTaxRateMapper() {
        // Given
        TaxJarDataToSalesTaxRateMapper taxJarDataToSalesTaxRateMapper = TaxJarDataToSalesTaxRateMapper.INSTANCE.INSTANCE;

        // When
        TaxJarDataToSalesTaxRateMapper secondTaxJarDataToSalesTaxRateMapper = mappersConfig.taxJarDataToSalesTaxRateMapper();

        // Then
        Assertions.assertEquals(taxJarDataToSalesTaxRateMapper, secondTaxJarDataToSalesTaxRateMapper);
    }
}
