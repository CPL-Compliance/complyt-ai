package com.complyt.v1.model;

import com.complyt.v1.model.customer.exemption.ExemptionTypeDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionTypeDtoTest {

    @Test
    void fullyTypeInit() {
        ExemptionTypeDto exemptionTypeDto = ExemptionTypeDto.FULLY;
        assertEquals(exemptionTypeDto,ExemptionTypeDto.FULLY);
    }

    @Test
    void partiallyTypeInit() {
        ExemptionTypeDto exemptionTypeDto = ExemptionTypeDto.PARTIALLY;
        assertEquals(exemptionTypeDto,ExemptionTypeDto.PARTIALLY);
    }
}
