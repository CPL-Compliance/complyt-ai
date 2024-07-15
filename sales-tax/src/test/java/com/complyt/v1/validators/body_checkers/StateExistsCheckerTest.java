package com.complyt.v1.validators.body_checkers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class StateExistsCheckerTest {

    @Test
    public void check_existingStatesInDifferentSpelling_StateExists() {
        assertEquals("New York", StateExistsChecker.check("ny"));
        assertEquals("California", StateExistsChecker.check("CA"));
        assertEquals("Texas", StateExistsChecker.check("texas"));
    }

    @Test
    public void check_NonExistingStates_returnsNull() {
        assertNull(StateExistsChecker.check("kalifornia"));
        assertNull(StateExistsChecker.check("notExisting"));
    }

    @Test
    public void check_nullAsState_returnsNull() {
        assertNull(StateExistsChecker.check(null));
    }

    @Test
    public void check_existingStatesWithWhiteSpaces_StateExists() {
        assertEquals("New York", StateExistsChecker.check("  ny  "));
        assertEquals("California", StateExistsChecker.check("  cA  "));
    }
}
