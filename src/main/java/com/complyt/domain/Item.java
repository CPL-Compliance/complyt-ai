package com.complyt.domain;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Item {
    private String unitPrice;
    private String quantity;
    private String totalPrice;
    private String description;
    private String name;
    private String taxCode;
}
