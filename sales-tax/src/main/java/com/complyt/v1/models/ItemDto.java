package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(name = "Item")
public class ItemDto {
    @Min(value = 0, message = "Unit Price's minimum value is 0")
    private float unitPrice;

    @Min(value = 0, message = "Quantity's minimum value is 0")
    private int quantity;

    @Min(value = 0, message = "Total Price's minimum value is 0")
    private float totalPrice;

    @NonNull
    @NotBlank(message = "Description may not be blank")
    @Size(min = 1, max = 256, message = "Description should be 1-256 characters maximum")
    private String description;

    @NonNull
    @NotBlank(message = "Name may not be blank")
    @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum")
    private String name;

    @NonNull
    @NotBlank(message = "Tax Code may not be blank")
    @Size(min = 1, max = 256, message = "Tax Code should be 1-256 characters maximum")
    private String taxCode;

    @NonNull
    @Valid
    @NotNull(message = "Jurisdictional Sales Tax Rules may not be null")
    private JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules;

    @NonNull
    @Valid
    @NotNull(message = "Jurisdictional Sales Tax Rules may not be null")
    private SalesTaxRateDto salesTaxRate;

    @NotNull(message = "Manual Sales Tax may not be null")
    private boolean manualSalesTax;

    @NotNull(message = "Manual Sales Tax Rate may not be null")
    @Min(value = 0, message = "manualSalesTaxRate's minimum value is 0")
    @DecimalMax(value = "0.2", message = "manualSalesTaxRate's maximum value is 0.2")
    private float manualSalesTaxRate;

    @NonNull
    @NotNull(message = "Tangible Category type may not be null")
    private TangibleCategoryDto tangibleCategory;

    @NonNull
    @NotNull(message = "Taxable Category type may not be null")
    private TaxableCategoryDto taxableCategory;
}