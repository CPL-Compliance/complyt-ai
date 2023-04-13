package com.example.complyt.config.web_clients;

import com.complyt.config.web_clients.WebClientWrapperProperties;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebClientWrapperPropertiesTest {

    String id;
    private WebClientWrapperProperties webClientWrapperProperties;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        webClientWrapperProperties = WebClientWrapperProperties.WebClientWrapperPropertiesStub();
    }

    @Test
    void Equals_SameWebClientWrapperProperties_ReturnTrue() {
        // Given
        WebClientWrapperProperties givenWebClientWrapperProperties = WebClientWrapperProperties.WebClientWrapperPropertiesStub();

        // When
        boolean isEquals = webClientWrapperProperties.equals(givenWebClientWrapperProperties);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void Builder_build() {
        // Given + When
        WebClientWrapperProperties actualWebClientWrapperProperties = WebClientWrapperProperties.builder().scheme("").path("").host("").key(new Pair<>("", "")).build();

        // Then
        assertEquals(webClientWrapperProperties, actualWebClientWrapperProperties);
    }

}