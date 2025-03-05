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
                method = RequestMethod.PUT,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Get ComplytSalesTaxRates by Address",
                        operationId = "getComplytSalesTaxRatesByAddress",
                        parameters = {
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "status",
                                        description = "Status of the internal rate (NEW,UPDATE,ARCHIVE)",
                                        examples = @ExampleObject(value = PutSalesTaxRatesByAddressApiInfo.status,
                                                name = PutSalesTaxRatesByAddressApiInfo.status))
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
                                                                @ExampleObject(value = PutSalesTaxRatesByAddressApiInfo.complytSalesTaxRatesExample)
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

public @interface PutSalesTaxRatesByAddressApiInfo {
    String status = "NEW";

    String complytSalesTaxRatesExample = """
            {
                 "complytId": "0817c932-c460-418e-ae03-a890cc310397",
                 "requestAddress": {
                     "address": {
                         "country": "US",
                         "state": "CA",
                         "zip": "90001",
                         "isPartial": true
                     },
                     "effectiveDate": "2024-01-01T00:00:00"
                 },
                 "matchedAddressData": {
                     "address": {
                         "city": "Los Angeles",
                         "country": "United States",
                         "county": "Los Angeles",
                         "state": "California",
                         "zip": "90001"
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
                     "stateRate": 0.0725,
                     "countyRate": 0,
                     "cityRate": 0.0225,
                     "mtaRate": 0,
                     "spdRate": 0,
                     "otherRate": 0,
                     "taxRate": 0.095
                 }
             }
            """;
}