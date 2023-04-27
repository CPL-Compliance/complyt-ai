package com.complyt.domain.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Value
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class InformationComponent {
    String name;
    String value;
}