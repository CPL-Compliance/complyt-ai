package com.complyt.domain.nexus.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefinitionTest {
    @Test
    public void Definition_GetCount_ReturnsCount() {
        // Given + When
        Definition definition = Definition.COUNT;

        // Then
        assertEquals(Definition.valueOf("COUNT"), definition);
    }

    @Test
    public void Definition_GetAmount_or_count_ReturnsAmount_or_count() {
        // Given + When
        Definition definition = Definition.AMOUNT_OR_COUNT;

        // Then
        assertEquals(Definition.valueOf("AMOUNT_OR_COUNT"), definition);
    }

    @Test
    public void Definition_GetAmount_ReturnsAmount() {
        // Given + When
        Definition definition = Definition.AMOUNT;

        // Then
        assertEquals(Definition.valueOf("AMOUNT"), definition);
    }

    @Test
    public void Definition_GetAmount_and_count_ReturnsAmount_and_count() {
        // Given + When
        Definition definition = Definition.AMOUNT_AND_COUNT;

        // Then
        assertEquals(Definition.valueOf("AMOUNT_AND_COUNT"), definition);
    }

}