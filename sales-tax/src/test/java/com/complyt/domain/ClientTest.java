package com.complyt.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ClientTest {
    private Client client;
    ObjectId id;
    String name;

    @BeforeEach
    void setup() {
        id = new ObjectId();
        name = "Client";
        client = new Client(id, name);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Client(id=" + id.toString() + ", name=Client)";

        // When
        String actualString = client.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test void Equals_SameClient_ReturnTrue() {
        // Given
        Client givenClient = new Client(id, name);

        // When
        boolean actualBoolean = client.equals(givenClient);

        // Then
        assertTrue(actualBoolean);
    }
}