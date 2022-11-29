package com.complyt.v1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TangibleCategoryDtoTest {

    @Test
    public void TangibleCategoryDto_GetTangible_ReturnTangible() {
        // Given + When
        TangibleCategoryDto tangibleCategoryDto = TangibleCategoryDto.TANGIBLE;

        // Then
        assertEquals(TangibleCategoryDto.valueOf("TANGIBLE"), tangibleCategoryDto);
    }

    @Test
    public void TangibleCategoryDto_GetIntangible_ReturnIntangible() {
        // Given + When
        TangibleCategoryDto tangibleCategoryDto = TangibleCategoryDto.INTANGIBLE;

        // Then
        assertEquals(TangibleCategoryDto.valueOf("INTANGIBLE"), tangibleCategoryDto);
    }
}