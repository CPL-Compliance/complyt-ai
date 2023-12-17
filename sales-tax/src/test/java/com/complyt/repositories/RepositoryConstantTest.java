package com.complyt.repositories;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryConstantTest {

    @Test
    public void checkDefaultConstant_Returns200() {
        assertEquals(25, RepositoryConstant.DEFAULT_PAGE_SIZE);
    }

}
