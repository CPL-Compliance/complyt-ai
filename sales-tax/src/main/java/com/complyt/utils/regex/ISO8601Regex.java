package com.complyt.utils.regex;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
public class ISO8601Regex {
     static final public String expression =
             "^(((20([0-9]{2})|2100)-((0[1-9])|(1[0-2]))-(0[1-9]|1\\d|2[0-8])" +
            "|(20([0-9]{2})|2100)-(((0[13578]|1[02])-31)|((0[13-9]|1[0-2])-(29|30)))|" +
             "(20(00|04|[2468][048]|[13579][26])|2100)-02-29)" +
            "(T([0-1]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d((\\.\\d{0,9})?))?" +
            "([zZ]|((-|\\+)(((0\\d|1[0-7])(:[0-5]\\d)?)|18(:00)?))?))?)$";

}