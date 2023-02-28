package com.complyt.utils.regex;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
/*
Holds a const regular expression to check if datetime is in correct ISO8601 format

(                                      # Start of the pattern
  (                                    # Start of the first group
    (                                  # Start of the first nested group
      20([0-9]{2})|2100                # Matches years between 2000 and 2100
    )-                                 # Matches a dash
    (                                  # Start of the second nested group
      (0[1-9])|(1[0-2])                # Matches months between 01 and 12
    )-                                 # Matches a dash
    (                                  # Start of the third nested group
      0[1-9]|1\d|2[0-8]                # Matches days between 01 and 28
    )                                  # End of the third nested group
  )                                    # End of the first nested group
  |                                    # Matches the alternation operator
  (                                    # Start of the second group
    20([0-9]{2})|2100-                # Matches years between 2000 and 2100
    (                                  # Start of the third nested group
      (0[13578]|1[02])-31              # Matches months with 31 days
      |                                # Matches the alternation operator
      (0[13-9]|1[0-2])-(29|30)         # Matches months with 30 days
    )                                  # End of the third nested group
  )                                    # End of the second group
  |                                    # Matches the alternation operator
  (                                    # Start of the third group
    20(00|04|[2468][048]|[13579][26])  # Matches leap years between 2000 and 2100
    |2100-                            # Matches the year 2100
    02-29                              # Matches February 29th
  )                                    # End of the third group
)                                      # End of the pattern
(T                                    # Matches the letter "T"
  ([0-1]\d|2[0-3]):[0-5]\d(:[0-5]\d   # Matches hours, minutes, and seconds
    ((\.\d{0,9})?))?                  # Matches optional decimal seconds
  ([zZ]                               # Matches "Z" or "z" for UTC time
    |(-|\+)                           # Matches a plus or minus sign for time offset
    (((0\d|1[0-7])(:[0-5]\d)?)|18(:00)?))? # Matches hours and minutes for time offset
)?)                                  # End of the pattern

 */
@Getter
public class ISO8601Regex {
     static final public String expression =
             "^(((20([0-9]{2})|2100)-((0[1-9])|(1[0-2]))-(0[1-9]|1\\d|2[0-8])" +
            "|(20([0-9]{2})|2100)-(((0[13578]|1[02])-31)|((0[13-9]|1[0-2])-(29|30)))|" +
             "(20(00|04|[2468][048]|[13579][26])|2100)-02-29)" +
            "(T([0-1]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d((\\.\\d{0,9})?))?" +
            "([zZ]|((-|\\+)(((0\\d|1[0-7])(:[0-5]\\d)?)|18(:00)?))?))?)$";

}