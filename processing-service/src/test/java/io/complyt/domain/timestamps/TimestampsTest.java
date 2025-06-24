package io.complyt.domain.timestamps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimestampsTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime earlier = now.minusDays(1);

    @Test
    void testConstructorAndGetters() {
        Timestamps timestamps = new Timestamps(earlier, now);

        assertEquals(earlier, timestamps.getCreatedDate());
        assertEquals(now, timestamps.getUpdatedDate());
    }

    @Test
    void testEqualsAndHashCode() {
        Timestamps t1 = new Timestamps(earlier, now);
        Timestamps t2 = new Timestamps(earlier, now);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testToString() {
        Timestamps timestamps = new Timestamps(earlier, now);
        String str = timestamps.toString();

        assertTrue(str.contains("createdDate"));
        assertTrue(str.contains("updatedDate"));
    }

    @Test
    void testWithMethod() {
        Timestamps original = new Timestamps(earlier, now);
        LocalDateTime newDate = now.plusDays(1);
        Timestamps modified = original.withUpdatedDate(newDate);

        assertEquals(original.getCreatedDate(), modified.getCreatedDate());
        assertEquals(newDate, modified.getUpdatedDate());
    }

    @Test
    void testJsonSerializationAndDeserialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Timestamps timestamps = new Timestamps(earlier, now);
        String json = mapper.writeValueAsString(timestamps);
        Timestamps deserialized = mapper.readValue(json, Timestamps.class);

        assertEquals(timestamps, deserialized);
    }
}
