package io.complyt.domain.sales_tax;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(RegisteredType.PENDING_STATE, RegisteredType.valueOf("PENDING_STATE"));
        assertEquals(RegisteredType.REGISTERED, RegisteredType.valueOf("REGISTERED"));
        assertEquals(RegisteredType.PENDING_CLIENT, RegisteredType.valueOf("PENDING_CLIENT"));
        assertEquals(RegisteredType.ONGOING_VDA, RegisteredType.valueOf("ONGOING_VDA"));
    }

    @Test
    void testEnumCount() {
        RegisteredType[] values = RegisteredType.values();
        assertEquals(4, values.length);
    }

    @Test
    void testEnumNames() {
        assertTrue(containsName(RegisteredType.values(), "PENDING_STATE"));
        assertTrue(containsName(RegisteredType.values(), "REGISTERED"));
        assertTrue(containsName(RegisteredType.values(), "PENDING_CLIENT"));
        assertTrue(containsName(RegisteredType.values(), "ONGOING_VDA"));
    }

    private boolean containsName(RegisteredType[] values, String name) {
        for (RegisteredType value : values) {
            if (value.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
