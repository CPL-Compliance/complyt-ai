package com.complyt.v1.config.regex;

public interface NotNullUndefinedRegex {
    String expression = "^(?!null$|undefined$).*";
}
