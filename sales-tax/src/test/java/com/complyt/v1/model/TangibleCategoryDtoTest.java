package com.complyt.v1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TangibleCategoryDtoTest {

    @Test
    public void TangibleCategoryDto_GetTangible_ReturnsTangible() {
        // Given + When
        TangibleCategoryDto tangibleCategoryDto = TangibleCategoryDto.TANGIBLE;

        // Then
        assertEquals(TangibleCategoryDto.valueOf("TANGIBLE"), tangibleCategoryDto);
    }

    @Test
    public void TangibleCategoryDto_GetIntangible_ReturnsIntangible() {
        // Given + When
        TangibleCategoryDto tangibleCategoryDto = TangibleCategoryDto.INTANGIBLE;

        // Then
        assertEquals(TangibleCategoryDto.valueOf("INTANGIBLE"), tangibleCategoryDto);
    }
}