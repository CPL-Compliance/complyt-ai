package com.complyt.domain.nexus.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TangibleCategoryTest {
    @Test
    public void TangibleCategory_GetTangible_ReturnsTangible() {
        // Given + When
        TangibleCategory tangibleCategory = TangibleCategory.TANGIBLE;

        // Then
        assertEquals(TangibleCategory.valueOf("TANGIBLE"), tangibleCategory);
    }

    @Test
    public void TangibleCategory_GetIntangible_ReturnsIntangible() {
        // Given + When
        TangibleCategory tangibleCategory = TangibleCategory.INTANGIBLE;

        // Then
        assertEquals(TangibleCategory.valueOf("INTANGIBLE"), tangibleCategory);
    }

}