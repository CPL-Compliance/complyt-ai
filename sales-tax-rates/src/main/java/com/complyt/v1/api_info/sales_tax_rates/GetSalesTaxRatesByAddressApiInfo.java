package com.complyt.v1.api_info.sales_tax_rates;

import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RouterOperations({
        @RouterOperation(
                method = RequestMethod.GET,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Get ComplytSalesTaxRates by Address",
                        operationId = "getComplytSalesTaxRatesByAddress",
                        parameters = {
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "effectiveDate",
                                        description = "date for getting the rate",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.effectiveDateExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.effectiveDateExample)),
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "state",
                                        description = "Address state",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.stateExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.stateExample)),
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "zip",
                                        description = "Address zip",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.zipExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.zipExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "country",
                                        description = "Address country",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.countryExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.countryExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "city",
                                        description = "Address city",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.cityExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.cityExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "street",
                                        description = "Address street",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.streetExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.streetExample)),

                        },
                        tags = "sales_tax_rates",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = SalesTaxRatesDto.class),
                                                        examples = {
                                                                @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.complytSalesTaxRatesExample)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "400",
                                        description = "Something is wrong with your request"
                                ),
                                @ApiResponse(
                                        responseCode = "401",
                                        description = "Unauthorized"
                                ),
                                @ApiResponse(
                                        responseCode = "403",
                                        description = "Forbidden"
                                ),
                                @ApiResponse(
                                        responseCode = "404",
                                        description = "SalesTaxRates Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})

public @interface GetSalesTaxRatesByAddressApiInfo {
    String countryExample = "US";
    String stateExample = "NY";
    String effectiveDateExample = "200-01-01";
    String cityExample = "New York";
    String streetExample = "541 6th Ave";
    String zipExample = "10011";

    String complytSalesTaxRatesExample = """
            {
                "complytId": "ccf486cc-9773-4081-8838-006a0f97d673",
                "requestAddress": {
                    "address": {
                        "country": "US",
                        "state": "Texas",
                        "zip": "73301",
                        "isPartial": true
                    },
                    "effectiveDate": "2024-01-01T00:00:00"
                },
                "matchedAddressData": {
                    "address": {
                        "city": "Austin",
                        "country": "United States",
                        "county": "Travis",
                        "state": "Texas",
                        "zip": "73301"
                    },
                    "scoring": {
                        "matchLevel": "EXCELLENT",
                        "score": 1.0,
                        "fieldScore": {
                            "countryMatch": "EXACT",
                            "stateMatch": "EXACT",
                            "cityMatch": "NO_MATCH",
                            "zipMatch": "EXACT"
                        }
                    }
                },
                "salesTaxRates": {
                    "stateRate": 0.0625,
                    "countyRate": 0,
                    "cityRate": 0.01,
                    "mtaRate": 0.01,
                    "spdRate": 0,
                    "otherRate": 0,
                    "taxRate": 0.0825
                }
            }
            """;
}