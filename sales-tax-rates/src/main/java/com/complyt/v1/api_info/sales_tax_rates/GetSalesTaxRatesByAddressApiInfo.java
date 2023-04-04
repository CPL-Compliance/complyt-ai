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
                        description = "Get SalesTaxRates by Address",
                        operationId = "getSalesTaxRatesByAddress",
                        parameters = {
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "state",
                                        description = "Address state",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.stateExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.stateExample)),
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
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "zip",
                                        description = "Address zip",
                                        examples = @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.zipExample,
                                                name = GetSalesTaxRatesByAddressApiInfo.zipExample))
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
                                                                @ExampleObject(value = GetSalesTaxRatesByAddressApiInfo.salesTaxRatesExample)
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
    String stateExample = "NY";
    String cityExample = "New York";
    String streetExample = "541 6th Ave";
    String zipExample = "10011";

    String salesTaxRatesExample = """
            {
                "cityDistrictRate": 0.0,
                "cityRate": 0.045,
                "countyDistrictRate": 0.00375,
                "countyRate": 0.0,
                "stateRate": 0.04,
                "taxRate": 0.08875
            }
            """;
}