package io.complyt.v1.api_info;

import io.complyt.v1.models.AddressDto;
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
                        description = "Get Validated Address ",
                        operationId = "getValidatedAddressByAddress",
                        parameters = {
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "country",
                                        description = "Address country",
                                        examples = {
                                                @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.countryAbbreviationExample,
                                                        name = GetComplytValidatedAddressByAddressApiInfo.countryAbbreviationExample),
                                                @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.countryExample,
                                                        name = GetComplytValidatedAddressByAddressApiInfo.countryExample)
                                        }),
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "state",
                                        description = "Address state",
                                        examples = {
                                                @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.stateExample,
                                                        name = GetComplytValidatedAddressByAddressApiInfo.stateExample),
                                                @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.stateAbbreviationExample,
                                                        name = GetComplytValidatedAddressByAddressApiInfo.stateAbbreviationExample),
                                        }),
                                @Parameter(in = ParameterIn.QUERY, required = true,
                                        name = "zip",
                                        description = "Address zip",
                                        examples = @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.zipExample,
                                                name = GetComplytValidatedAddressByAddressApiInfo.zipExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "city",
                                        description = "Address city",
                                        examples = @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.cityExample,
                                                name = GetComplytValidatedAddressByAddressApiInfo.cityExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "street",
                                        description = "Address street",
                                        examples = @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.streetExample,
                                                name = GetComplytValidatedAddressByAddressApiInfo.streetExample))
                        },
                        tags = "validated_address",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = AddressDto.class),
                                                        examples = {
                                                                @ExampleObject(value = GetComplytValidatedAddressByAddressApiInfo.complytSalesTaxRatesExample)
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
                                        description = "Address Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})

public @interface GetComplytValidatedAddressByAddressApiInfo {
    //todo : complete this file
    String countryExample = "United States";
    String countryAbbreviationExample = "USA";
    String stateExample = "New York";
    String stateAbbreviationExample = "NY";
    String cityExample = "New York";
    String streetExample = "160 Broadway";
    String zipExample = "10038";

    String complytSalesTaxRatesExample = """
            {
                "address": {
                    "city": "New York",
                    "country": "USA",
                    "county": "New York",
                    "state": "New York",
                    "street": "160 Broadway",
                    "zip": "10038",
                    "isPartial": false
                }
            }
            """;
}
