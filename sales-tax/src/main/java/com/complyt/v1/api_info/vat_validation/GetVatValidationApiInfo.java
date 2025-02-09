package com.complyt.v1.api_info.vat_validation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
                        description = "Validate VAT Details",
                        operationId = "validateVatDetails",
                        parameters = {
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "countryCode",
                                        description = "Country code for VAT validation",
                                        required = true,
                                        examples = @ExampleObject(value = GetVatValidationApiInfo.countryCodeExample,
                                                name = GetVatValidationApiInfo.countryCodeExample)),
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "vatNumber",
                                        description = "VAT number to validate",
                                        required = true,
                                        examples = @ExampleObject(value = GetVatValidationApiInfo.vatNumberExample,
                                                name = GetVatValidationApiInfo.vatNumberExample))
                        },
                        tags = "vat",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful VAT validation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = String.class),
                                                        examples = {
                                                                @ExampleObject(value = GetVatValidationApiInfo.validVatResponseExample)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "400",
                                        description = "Invalid request format"
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
                                        description = "VAT details not found"
                                ),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Server Error"
                                )
                        }))
})

public @interface GetVatValidationApiInfo {
    String countryCodeExample = "BE";
    String vatNumberExample = "0835221567";

    String validVatResponseExample = """
            {
                "countryCode": "BE",
                "countryName": "Belgium",
                "vatNumber": "0835221567",
                "valid": true,
                "name": "BV BE³-PROJECTS",
                "address": "Kasteeldreef 9 2940 Stabroek"
            }
            """;
}