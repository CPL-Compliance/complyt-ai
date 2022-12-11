package com.complyt.business.nexus;

import com.complyt.domain.nexus.enums.TimeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationDateCreatorTest {

    private ApplicationDateCreator applicationDateCreator;
    private LocalDateTime referenceDate;

    @BeforeEach
    void setUp() {
        applicationDateCreator = new ApplicationDateCreator();
        referenceDate = LocalDateTime.now();
    }

    @Test
    void create_PreviousCalenderYearPassed_ReturnsApplyNextYearDate() {
        // Given
        TimeFrame timeFrame = TimeFrame.PREVIOUS_CALENDER_YEAR;

        LocalDateTime expectedApplicationDate = referenceDate
                .plusYears(1)
                .with(firstDayOfYear())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime actualApplicationDate = applicationDateCreator.create(timeFrame, referenceDate);

        // When + Then
        assertEquals(expectedApplicationDate, actualApplicationDate);

    }

    @Test
    void create_YearFromSeptemberToSeptemberPassedAndDateIsPriorToSeptember30Th_ReturnsApplyNextSeptember() {
        // Given
        TimeFrame timeFrame = TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER;
        LocalDateTime september30 = LocalDateTime.now()
                .withMonth(9)
                .withDayOfMonth(30)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime referenceDate = LocalDateTime.now().withMonth(9).withDayOfMonth(29);
        LocalDateTime actualApplicationDate = applicationDateCreator.create(timeFrame, referenceDate);

        // When + Then
        assertEquals(september30, actualApplicationDate);
    }

    @Test
    void create_YearFromSeptemberToSeptemberPassedAndDateIsLaterThenSeptember30Th_ReturnsApplyNextSeptember() {
        // Given
        TimeFrame timeFrame = TimeFrame.YEAR_FROM_SEPTEMBER_TO_SEPTEMBER;
        LocalDateTime nextSeptember30 = LocalDateTime.now()
                .withMonth(9)
                .withDayOfMonth(30)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .plusYears(1);

        LocalDateTime referenceDate = LocalDateTime.now().withMonth(10).withDayOfMonth(1);
        LocalDateTime actualApplicationDate = applicationDateCreator.create(timeFrame, referenceDate);

        // When + Then
        assertEquals(nextSeptember30, actualApplicationDate);
    }


    @Test
    void create_CurrentCalenderYearPassed_ReturnsApplyNextYearDate() {
        // Given
        TimeFrame timeFrame = TimeFrame.CURRENT_CALENDER_YEAR;
        LocalDateTime actualApplicationDate = applicationDateCreator.create(timeFrame, referenceDate);

        // When + Then
        assertEquals(actualApplicationDate, referenceDate);
    }

    @Test
    void create_NullDatePassed_ThrowsException() {
        // Given
        TimeFrame timeFrame = TimeFrame.CURRENT_CALENDER_YEAR;
        LocalDateTime nullReferenceDate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            applicationDateCreator.create(timeFrame, nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void create_NullTimeFramePassed_ThrowsException() {
        // Given
        TimeFrame nullTimeFrame = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            applicationDateCreator.create(nullTimeFrame, referenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "timeFrame is marked non-null but is null");
    }

}