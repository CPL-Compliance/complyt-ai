package com.complyt.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringCheckerTest {

    @Test
    void givenValidInputs_ShouldReturnTrue() {
        boolean result = StringChecker.isInputValid("inputs","anotherInput");
        Assertions.assertTrue(result);
    }

    @Test
    void givenInvalidBlankInput_ShouldReturnFalse() {
        boolean result = StringChecker.isInputValid("","anotherInput");
        Assertions.assertFalse(result);
    }

    @Test
    void givenInvalidNullInput_ShouldReturnFalse() {
        boolean result = StringChecker.isInputValid("input",null);
        Assertions.assertFalse(result);
    }
}
