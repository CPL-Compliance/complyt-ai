package com.complyt.domain;

import lombok.*;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class Item {
    private final String price;
    private final String quantity;
    private final String description;
    private final String name;
    private final String taxCode;
}
