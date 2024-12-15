package com.complyt.domain.sales_tax.complyt_sales_tax_rates;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
@With
public record CommonAddress(String country,
                            String state,
                            String county,
                            String city,
                            Boolean isUnincorporated,
                            Boolean hasPlusFourZipCode,
                            String zip,
                            Integer lowerPlusFourDigits,
                            Integer upperPlusFourDigits,
                            String street,
                            Boolean isPartial
) {
}

