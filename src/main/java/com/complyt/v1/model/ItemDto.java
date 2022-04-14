package com.complyt.v1.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class ItemDto {
    private String price;
    private String quantity;
    private String description;
    private String name;
    private String taxCode;
}