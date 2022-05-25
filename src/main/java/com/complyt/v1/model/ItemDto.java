package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(name = "Item")
public class ItemDto {
    private float unitPrice;
    private int quantity;
    private float totalPrice;
    private String description;
    private String name;
    private String taxCode;
    private SalesTaxRateDto salesTaxRate;
}