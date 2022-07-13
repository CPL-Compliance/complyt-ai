package com.complyt.domain;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class State {
    private String abbreviation;
    private String code;
    private String name;
}