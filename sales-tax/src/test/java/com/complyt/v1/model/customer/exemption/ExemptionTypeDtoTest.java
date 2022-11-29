package com.complyt.v1.model.customer.exemption;

import com.complyt.v1.model.customer.exemption.ExemptionTypeDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionTypeDtoTest {

    @Test
    void fullyTypeInit() {
        // Given + When
        ExemptionTypeDto exemptionTypeDto = ExemptionTypeDto.FULLY;

        // Then
        assertEquals(ExemptionTypeDto.valueOf("FULLY"),exemptionTypeDto);
    }

    @Test
    void partiallyTypeInit() {
        // Given + When
        ExemptionTypeDto exemptionTypeDto = ExemptionTypeDto.PARTIALLY;

        // Then
        assertEquals(ExemptionTypeDto.valueOf("PARTIALLY"),exemptionTypeDto);
    }
}
