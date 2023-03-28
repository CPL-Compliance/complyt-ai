package com.complyt.domain.fast_tax;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class InformationComponent {
    private String name;
    private String value;
}