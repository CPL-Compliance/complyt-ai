package com.complyt.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Item {
    private String price;
    private String quantity;
    private String description;
    private String name;
    private String taxCode;
}
