package io.complyt.domain.timestamps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class TimestampsTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime later = now.plusHours(1);

    @Test
    void testConstructorAndGetters() {
        Timestamps timestamps = new Timestamps(now, later);

        assertEquals(now, timestamps.createdDate());
        assertEquals(later, timestamps.updatedDate());
    }

    @Test
    void testEqualsAndHashCode() {
        Timestamps ts1 = new Timestamps(now, later);
        Timestamps ts2 = new Timestamps(now, later);

        assertEquals(ts1, ts2);
        assertEquals(ts1.hashCode(), ts2.hashCode());
    }

    @Test
    void testToString() {
        Timestamps timestamps = new Timestamps(now, later);
        String str = timestamps.toString();

        assertTrue(str.contains("createdDate"));
        assertTrue(str.contains("updatedDate"));
    }

    @Test
    void testWithMethods() {
        Timestamps original = new Timestamps(now, later);
        Timestamps modified = original.withUpdatedDate(now);

        assertEquals(now, modified.updatedDate());
        assertEquals(original.createdDate(), modified.createdDate());
        assertNotEquals(original, modified);
    }

    @Test
    void testJsonDeserializationWithJsonProperty() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = String.format(
                "{\"createdDate\":\"%s\",\"updatedDate\":\"%s\"}",
                now, later
        );

        Timestamps result = mapper.readValue(json, Timestamps.class);

        assertEquals(now, result.createdDate());
        assertEquals(later, result.updatedDate());
    }


    @Test
    void testJsonSerialization() throws JsonProcessingException {
        Timestamps timestamps = new Timestamps(now, later);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = mapper.writeValueAsString(timestamps);

        assertTrue(json.contains("\"createdDate\""));
        assertTrue(json.contains("\"updatedDate\""));
    }
}
