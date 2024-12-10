//package com.complyt.v1.mappers;
//
//import com.complyt.domain.ComplytSalesTaxRates;
//import com.complyt.v1.config.error_messages.DtoErrorMessages;
//import com.complyt.v1.model.common_sales_tax_rates.CommonSalesTaxRatesDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import testUtils.TestUtilities;
//
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ComplytSalesTaxRatesMapperTest {
//    private ComplytSalesTaxRates complytSalesTaxRates;
//    private CommonSalesTaxRatesDto commonSalesTaxRatesDto;
//
//    @Test
//    void commonSalesTaxRatesDtoToComplytSalesTaxRates_commonSalesTaxRatesDto_ReturnComplytSalesTaxRates() {
//        // Given
//        commonSalesTaxRatesDto = TestUtilities.createCommonSalesTaxRatesDto(BigDecimal.valueOf(0.1));
//        complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates()
//                .
//
//        // When
//        String actualDateString = StringToLocalDateTimeMapper.INSTANCE.localDateTimeToString(givenLocalDateTime);
//
//        // Then
//        assertEquals(dateString, actualDateString);
//    }
//
//    @Test
//    void localDateTimeNull_String_ReturnsNull() {
//        // Given
//        LocalDateTime givenLocalDateTime = null;
//
//        // When
//        String actualDateString = StringToLocalDateTimeMapper.INSTANCE.localDateTimeToString(givenLocalDateTime);
//
//        // Then
//        assertNull(actualDateString);
//    }
//
//    @Test
//    void StringJustDate_LocalDateTime_ReturnLocalDateTime() throws ParseException {
//        // Given
//        String givenLocalDateTimeString = "2023-03-17";
//
//        // When
//        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);
//
//        // Then
//        assertEquals(dateLocalDateTime, actualLocalDateTime);
//    }
//
//    @Test
//    void StringDateWithExectTime_LocalDateTime_ReturnLocalDateTime() throws ParseException {
//        // Given
//        String givenLocalDateTimeString = "2023-03-17T00:00";
//
//        // When
//        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);
//
//        // Then
//        assertEquals(dateLocalDateTime, actualLocalDateTime);
//    }
//
//    @Test
//    void StringDateWithExactTimeAndOffset_LocalDateTime_ReturnLocalDateTime() throws ParseException {
//        // Given
//        String givenLocalDateTimeString = "2023-03-17T00:00+00:00";
//
//        // When
//        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);
//
//        // Then
//        assertEquals(dateLocalDateTime, actualLocalDateTime);
//    }
//
//    @Test
//    void StringDateWithExactTimeAndOffsetOfZ_LocalDateTime_ReturnLocalDateTime() throws ParseException {
//        // Given
//        String givenLocalDateTimeString = "2023-03-17T00:00Z";
//
//        // When
//        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);
//
//        // Then
//        assertEquals(dateLocalDateTime, actualLocalDateTime);
//    }
//
//    @Test
//    void StringDateWithWrongFormat_LocalDateTime_ThrowsParseException() throws ParseException {
//        // Given
//        String givenLocalDateTimeString = "23-03-17";
//
//        // When
//        Exception exception = assertThrows(ParseException.class,
//                () -> StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString));
//
//        // Then
//        assertEquals(exceptionMessage, exception.getMessage());
//    }
//
//    @Test
//    void StringDateNull_LocalDateTime_ReturnsNull() throws ParseException {
//        // Given
//        String givenLocalDateTimeString = null;
//
//        // When
//        LocalDateTime actualLocalDateTime = StringToLocalDateTimeMapper.INSTANCE.parseStringToLocalDateTime(givenLocalDateTimeString);
//
//        // Then
//        assertNull(actualLocalDateTime);
//    }
//
//}