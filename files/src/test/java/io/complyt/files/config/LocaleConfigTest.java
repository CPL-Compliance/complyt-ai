package io.complyt.files.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class LocaleConfigTest {

    LocaleConfig localeConfig;

    @Test
    void setUTCTimeZone() {
        // Given
        localeConfig = new LocaleConfig();

        // When
        localeConfig.setUTCTimeZone();

        // Then
        assertEquals(TimeZone.getDefault(), TimeZone.getTimeZone("UTC"));
    }
}