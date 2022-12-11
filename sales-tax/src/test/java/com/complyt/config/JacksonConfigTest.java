package com.complyt.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonConfigTest {

    private JacksonConfig jacksonConfig;

    @BeforeEach
    void setup() {
        jacksonConfig = new JacksonConfig();
    }

    @Test
    void customizer_gotJackson2ObjectMapperBuilderCustomizer() {
        // Given
        Jackson2ObjectMapperBuilder actualJackson2ObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
        Jackson2ObjectMapperBuilder expectedJackson2ObjectMapperBuilder = actualJackson2ObjectMapperBuilder.serializerByType(ObjectId.class, new ToStringSerializer());

        // When
        Jackson2ObjectMapperBuilderCustomizer actualJackson2ObjectMapperBuilderCustomizer = jacksonConfig.customizer();
        actualJackson2ObjectMapperBuilderCustomizer.customize(actualJackson2ObjectMapperBuilder);

        // Then
        assertEquals(expectedJackson2ObjectMapperBuilder, actualJackson2ObjectMapperBuilder);
    }
}