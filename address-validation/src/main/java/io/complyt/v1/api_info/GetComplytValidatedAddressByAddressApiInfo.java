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
                                                name = GetComplytValidatedAddressByAddressApiInfo.streetExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "county",
                                        description = "Address county",
                                        examples = @ExampleObject(value = GetComplytResolveAddressByAddressApiInfo.cityExample,
                                                name = GetComplytResolveAddressByAddressApiInfo.cityExample))
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
                                        description = "Something is wrong with your request:\n ERR-ADDR-001: The Validate Address endpoint returns valid address suggestions based on the provided input address."
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
    String countryExample = "United States";
    String countryAbbreviationExample = "USA";
    String stateExample = "New York";
    String stateAbbreviationExample = "NY";
    String cityExample = "New York";
    String streetExample = "160 Broadway";
    String zipExample = "10038";

    String complytSalesTaxRatesExample = """
            {
                "matchedAddresses": [
                    {
                        "address": {
                            "city": "Phoenix",
                            "country": "United States",
                            "county": "Maricopa",
                            "state": "Arizona",
                            "street": "S 3rd Ave",
                            "zip": "85041-5705"
                        },
                        "scoring": {
                            "matchLevel": "EXCELLENT",
                            "score": 0.96,
                            "fieldScore": {
                                "countryMatch": "EXACT",
                                "stateMatch": "EXACT",
                                "cityMatch": "EXACT",
                                "streetMatch": "GOOD",
                                "zipMatch": "EXACT"
                            }
                        }
                    },
                    {
                        "address": {
                            "city": "Phoenix",
                            "country": "United States",
                            "county": "Maricopa",
                            "state": "Arizona",
                            "street": "S 3rd St",
                            "zip": "85042-4206"
                        },
                        "scoring": {
                            "matchLevel": "EXCELLENT",
                            "score": 0.92,
                            "fieldScore": {
                                "countryMatch": "EXACT",
                                "stateMatch": "EXACT",
                                "cityMatch": "EXACT",
                                "streetMatch": "GOOD",
                                "zipMatch": "GOOD"
                            }
                        }
                    }
                ],
                "requestAddress": {
                    "city": "Phoenix",
                    "country": "USA",
                    "state": "Arizona",
                    "street": "6001 W 3rd St",
                    "zip": "85041-5705",
                    "isPartial": false
                }
            }
            """;
}
