package com.complyt.utils;

public final class StringChecker {

    public static boolean isInputValid(final String... inputs){
        for (String input : inputs){
            if (input == null || input.isBlank())
                return false;
        }
        return true;
    }
}
