package com.complyt.v1.config.regex;

public interface NumericRegex {
    String pageRegex = "\\d+";
    String sizeRegex = "^[1-9]+\\d*$"; // Has to be positive
}
