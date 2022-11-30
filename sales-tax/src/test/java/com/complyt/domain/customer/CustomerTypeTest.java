package com.complyt.domain.customer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTypeTest {

    @Test
    public void CustomerType_GetRetail_ReturnRetail() {
        // Given + When
        CustomerType customerType = CustomerType.RETAIL;

        // Then
        assertEquals(CustomerType.valueOf("RETAIL"), customerType);
    }

    @Test
    public void CustomerType_GetMarketplace_ReturnMarketplace() {
        // Given + When
        CustomerType customerType = CustomerType.MARKETPLACE;

        // Then
        assertEquals(CustomerType.valueOf("MARKETPLACE"), customerType);
    }

    @Test
    public void CustomerType_GetReseller_ReturnReseller() {
        // Given + When
        CustomerType customerType = CustomerType.RESELLER;

        // Then
        assertEquals(CustomerType.valueOf("RESELLER"), customerType);
    }

    @Test
    public void CustomerType_GetRetail_exempt_ReturnRetail_exempt() {
        // Given + When
        CustomerType customerType = CustomerType.RETAIL_EXEMPT;

        // Then
        assertEquals(CustomerType.valueOf("RETAIL_EXEMPT"), customerType);
    }
}