package com.complyt.v1.config.regex;

public interface NotNullUndefinedOrBlankRegex {
    String expression = "^(?!null$|undefined$|\\s*$).+";
}
