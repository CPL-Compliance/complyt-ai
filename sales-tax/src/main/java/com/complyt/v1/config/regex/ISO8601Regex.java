package com.complyt.v1.config.regex;

/*
Holds a const regular expression to check if datetime is in correct ISO8601 format

Short explanation:
********************
the FIRST part checks for the format yyyy-mm-dd:
# the year must be 4 digits, and month and days must be 2 digits (with 0 in the begging if one digit)

(((19[7-9][0-9]|20([0-9]{2})|2100)-((0[1-9])|(1[0-2]))-(0[1-9]|1\d|2[0-8])
# checks up to the 28th of each month (every month have at least 28 days)

(19[7-9][0-9]|20([0-9]{2})|2100)-(((0[13578]|1[02])-31)|((0[13-9]|1[0-2])-(29|30)))
# checks for months with 29,30 and 31 days

(19(8[048]|[79][26])|20(00|04|[2468][048]|[13579][26])|2100)-02-29)
# checks for leap years - years where february has 29 days

the SECOND part is OPTIONAL, and checks for the time and the offset:

(T([0-1]\d|2[0-3]):[0-5]\d(:[0-5]\d((\.\d{0,9})?))?
# checks if the format is Thh:mm:ss.milliseconds, where the seconds and milliseconds are optional
# the time after the T can be up to 23:59:59.999999999

([zZ]|((-|\+)(((0\d|1[0-7])(:[0-5]\d)?)|18(:00)?))?))?)$
# checks for z or Z in the end of the last time part,
# OR checks if there is an offset in the format +/-hh:mm up to +/-18:00

Extended explanation:
********************
(                                      # Start of the pattern
  (                                    # Start of the first group
    (                                  # Start of the first nested group
      19[7-9][0-9]|20([0-9]{2})|2100   # Matches years between 2000 and 2100
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
    19[7-9][0-9]|20([0-9]{2})|2100-    # Matches years between 2000 and 2100
    (                                  # Start of the third nested group
      (0[13578]|1[02])-31              # Matches months with 31 days
      |                                # Matches the alternation operator
      (0[13-9]|1[0-2])-(29|30)         # Matches months with 30 days
    )                                  # End of the third nested group
  )                                    # End of the second group
  |                                    # Matches the alternation operator
  (                                    # Start of the third group
    19(8[048]|[79][26])|               # Matches leap years between 1972 and 1996
    20(00|04|[2468][048]|[13579][26])  # Matches leap years between 2000 and 2096
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


Accepted formats and examples:
*******************************
yyyy-mm-dd
yyyy-mm-ddThh:mm
yyyy-mm-ddThh:mm. <from 0 to 9 digits after the dot>
yyyy-mm-ddThh:mm.z
yyyy-mm-ddThh:mm.Z
yyyy-mm-ddThh:mm.+hh:mm
yyyy-mm-ddThh:mm.-hh:mm

2015-08-02
2015-08-02T19:12
2020-03-27T03:40:59
2020-03-27T03:40:59.
2020-03-27T03:40:59.999999999
2020-03-27T03:40:59.0001z
2020-03-27T03:40:59.003Z
2020-03-27T03:40:59.1+09:58
2020-03-27T03:40:59.-18:00
2020-03-27T03:40:59+17:59

 */
public interface ISO8601Regex {
    String expression =
            "^(((19[7-9][0-9]|20([0-9]{2})|2100)-((0[1-9])|(1[0-2]))-(0[1-9]|1\\d|2[0-8])" +
                    "|(19[7-9][0-9]|20([0-9]{2})|2100)-(((0[13578]|1[02])-31)|((0[13-9]|1[0-2])-(29|30)))" +
                    "|(19(8[048]|[79][26])|20([2468][048]|[13579][26]))-02-29)" +
                    "(T([0-1]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d((\\.\\d{0,9})?))?" +
                    "([zZ]|((-|\\+)(((0\\d|1[0-7])(:[0-5]\\d)?)|18(:00)?))?))?)$";

}