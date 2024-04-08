package com.example.complyt.v1.model.gt;

import com.complyt.v1.model.gt.ComplytGtRatesDto;
import com.complyt.v1.model.gt.GtRatesDto;
import com.complyt.v1.model.gt.GtAddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComplytGtRatesDtoTest {

    private ComplytGtRatesDto complytGtRatesDto;

    @BeforeEach
    void setup() {
        GtAddressDto gtAddressDto = new GtAddressDto("Canada", "Quebec");
        GtRatesDto gtRatesDto = new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975));
        complytGtRatesDto = new ComplytGtRatesDto(gtAddressDto, gtRatesDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ComplytGtRatesDto[gtAddress=" + complytGtRatesDto.gtAddress() +
                ", gtRates=" + complytGtRatesDto.gtRates() + "]";

        // When
        String actualString = complytGtRatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
