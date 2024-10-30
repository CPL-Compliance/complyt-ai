package com.complyt.v1.config.regex;

public interface NumericRegex {
    String pageRegex = "^[1-9]\\d*$";
    String sizeRegex = "^[1-9]+\\d*$"; // Has to be positive
}
