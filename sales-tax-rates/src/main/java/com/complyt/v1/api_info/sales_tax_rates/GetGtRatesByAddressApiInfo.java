package com.complyt.v1.api_info.sales_tax_rates;

import com.complyt.v1.model.SalesTaxRatesDto;
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
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "country",
                                        description = "Address country",
                                        examples = @ExampleObject(value = GetGtRatesByAddressApiInfo.countryExample,
                                                name = GetGtRatesByAddressApiInfo.countryExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "region",
                                        description = "Address region",
                                        examples = @ExampleObject(value = GetGtRatesByAddressApiInfo.regionExample,
                                                name = GetGtRatesByAddressApiInfo.regionExample))
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
                                                                @ExampleObject(value = GetGtRatesByAddressApiInfo.complytSalesTaxRatesExample)
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

public @interface GetGtRatesByAddressApiInfo {
    String countryExample = "Canada";
    String regionExample = "Quebec";

    String complytSalesTaxRatesExample = """
            {
                "address": {
                    "country": "Canada",
                    "region": "Quebec",
                },
                "gtRates": {
                    "countryRate": 0.05,
                    "regionRate": 0.0975,
                    "taxRate": 0.14975
                }
            }
            """;
}