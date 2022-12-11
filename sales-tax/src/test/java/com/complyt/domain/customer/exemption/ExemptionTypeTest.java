package com.complyt.domain.customer.exemption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionTypeTest {

    @Test
    void fullyTypeInit() {
        // Given + When
        ExemptionType exemptionType = ExemptionType.FULLY;

        // Then
        assertEquals(ExemptionType.valueOf("FULLY"), exemptionType);
    }

    @Test
    void partiallyTypeInit() {
        // Given + When
        ExemptionType exemptionType = ExemptionType.PARTIALLY;

        // Then
        assertEquals(ExemptionType.valueOf("PARTIALLY"), exemptionType);
    }
}
