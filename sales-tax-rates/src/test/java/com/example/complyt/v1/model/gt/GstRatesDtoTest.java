package com.example.complyt.v1.model.gt;

import com.complyt.v1.model.gt.GtRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GstRatesDtoTest {

    private GtRatesDto gstRatesDto;

    @BeforeEach
    void setup() {
        gstRatesDto = new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975));
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "GtRatesDto[countryRate=" + gstRatesDto.countryRate() +
                ", regionRate=" + gstRatesDto.regionRate() +
                ", taxRate=" + gstRatesDto.taxRate() + "]";

        // When
        String actualString = gstRatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}
