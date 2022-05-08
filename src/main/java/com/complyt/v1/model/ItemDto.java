package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(name = "Item")
public class ItemDto {
    private String unitPrice;
    private String quantity;
    private String totalPrice;
    private String description;
    private String name;
    private String taxCode;
}