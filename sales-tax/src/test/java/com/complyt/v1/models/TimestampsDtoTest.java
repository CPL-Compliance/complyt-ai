package com.complyt.v1.models;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class TimestampsDtoTest {

    private TimestampsDto timestampsDto;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setup() {
        String createdDate = "2002-02-02T02:02:02";
        String updatedDate = "2003-03-03T03:03:03";
        timestampsDto = new TimestampsDto(createdDate, updatedDate);
    }

    @Test
    void init_DatesReceivedWithHour_ReturnsDate() {
        // Given
        String createdDate = "2015-05-25T13:05:45";
        String updatedDate = "2015-05-25T13:05:45";
        LocalDateTime expectedCreatedDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedUpdatedDate = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
        LocalDateTime actualCreatedDate = LocalDateTime.parse(timeStampsDto.createdDate());
        LocalDateTime actualUpdatedDate = LocalDateTime.parse(timeStampsDto.updatedDate());

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate, actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate, actualUpdatedDate);
    }

    @Test
    void init_DatesReceivedWithNoHour_ReturnsBeginningOfDay() {
        // Given
        String createdDate = "2015-05-25";
        String updatedDate = "2015-05-25";
        LocalDateTime expectedCreatedDate = LocalDate.parse(createdDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
        LocalDateTime expectedUpdatedDate = LocalDate.parse(updatedDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);

        // When
        TimestampsDto timeStampsDto = new TimestampsDto(createdDate, updatedDate);
        LocalDateTime actualCreatedDate = LocalDate.parse(timeStampsDto.createdDate(), DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);
        LocalDateTime actualUpdatedDate = LocalDate.parse(timeStampsDto.updatedDate(), DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0);

        // Then
        Assertions.assertNotNull(actualCreatedDate);
        Assertions.assertNotNull(actualUpdatedDate);
        Assertions.assertEquals(expectedCreatedDate, actualCreatedDate);
        Assertions.assertEquals(expectedUpdatedDate, actualUpdatedDate);
    }

    @Test
    void withUpdateDate_DifferentDate_ReturnTimestampsDto() {
        // Given
        String createdDate = "2002-02-02T02:02:02";
        String updatedDate = "2004-04-04T04:04:04";

        TimestampsDto expectedTimestampsDto = new TimestampsDto(createdDate, updatedDate);
        String differentDate = "2004-04-04T04:04:04";

        // When
        TimestampsDto actualTimestampsDto = timestampsDto.withUpdatedDate(updatedDate);


        // Then
        assertEquals(expectedTimestampsDto, actualTimestampsDto);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TimestampsDto[createdDate=" + timestampsDto.createdDate() +
                ", updatedDate=" + timestampsDto.updatedDate() + "]";

        // When
        String actualString = timestampsDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
