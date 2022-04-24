package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Schema(name = "Item")
public class ItemDto {
    private String price;
    private String quantity;
    private String description;
    private String name;
    private String taxCode;
}