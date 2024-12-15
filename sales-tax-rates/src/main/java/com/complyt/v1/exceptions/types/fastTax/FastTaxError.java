package com.complyt.v1.exceptions.types.fastTax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@With
@AllArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class FastTaxError {
    String desc;
    String number;
}
