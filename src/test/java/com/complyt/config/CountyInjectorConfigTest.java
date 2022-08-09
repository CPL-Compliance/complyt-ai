package com.complyt.config;

import com.complyt.business.utils.transaction_data_injector.FastTaxCountyInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CountyInjectorConfigTest {

    private CountyInjectorConfig countyInjectorConfig;

    @BeforeEach
    void setup() {
        countyInjectorConfig = new CountyInjectorConfig();
    }

    @Test
    void fastTaxCountyInjector_CreateInstance_ReturnInstance() {
        FastTaxCountyInjector expectedFastTaxCountyInjector = new FastTaxCountyInjector();

        FastTaxCountyInjector actualFastTaxCountyInjector = countyInjectorConfig.fastTaxCountyInjector();

        assertEquals(expectedFastTaxCountyInjector,actualFastTaxCountyInjector);
    }

}
