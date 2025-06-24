package io.complyt.domain;

import io.complyt.domain.nexus.SalesTaxTracking;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClassMapperTest {

    @Test
    void testClassMapContainsTransaction() {
        Class<? extends ComplytIdProperty> clazz = ClassMapper.CLASS_MAP.get("Transaction");
        assertNotNull(clazz);
        assertEquals(Transaction.class, clazz);
    }

    @Test
    void testClassMapContainsSalesTaxTracking() {
        Class<? extends ComplytIdProperty> clazz = ClassMapper.CLASS_MAP.get("SalesTaxTracking");
        assertNotNull(clazz);
        assertEquals(SalesTaxTracking.class, clazz);
    }

    @Test
    void testClassMapDoesNotContainUnknownKey() {
        assertNull(ClassMapper.CLASS_MAP.get("UnknownType"));
    }

    @Test
    void testClassMapIsImmutable() {
        assertThrows(UnsupportedOperationException.class, () ->
                ClassMapper.CLASS_MAP.put("NewType", Transaction.class));
    }

    @Test
    void testClassMapSize() {
        Map<String, Class<? extends ComplytIdProperty>> map = ClassMapper.CLASS_MAP;
        assertEquals(2, map.size());
    }
}
