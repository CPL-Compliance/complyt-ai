package com.complyt.v1.model.customer;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTypeDtoTest {

    @Test
    public void CustomerTypeDto_GetRetail_ReturnRetail() {
        // Given + When
        CustomerTypeDto customerTypeDto = CustomerTypeDto.RETAIL;

        // Then
        assertEquals(CustomerTypeDto.valueOf("RETAIL"), customerTypeDto);
    }

    @Test
    public void CustomerTypeDto_GetMarketplace_ReturnMarketplace() {
        // Given + When
        CustomerTypeDto customerTypeDto = CustomerTypeDto.MARKETPLACE;

        // Then
        assertEquals(CustomerTypeDto.valueOf("MARKETPLACE"), customerTypeDto);
    }

    @Test
    public void CustomerTypeDto_GetReseller_ReturnReseller() {
        // Given + When
        CustomerTypeDto customerTypeDto = CustomerTypeDto.RESELLER;

        // Then
        assertEquals(CustomerTypeDto.valueOf("RESELLER"), customerTypeDto);
    }

    @Test
    public void CustomerTypeDto_GetRetail_exempt_ReturnRetail_exempt() {
        // Given + When
        CustomerTypeDto customerTypeDto = CustomerTypeDto.RETAIL_EXEMPT;

        // Then
        assertEquals(CustomerTypeDto.valueOf("RETAIL_EXEMPT"), customerTypeDto);
    }
}