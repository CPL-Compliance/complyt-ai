package com.complyt.v1.api_info.exemption;

import com.complyt.v1.models.customer.exemption.ExemptionDto;
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
                method = RequestMethod.PATCH,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "patch exemption",
                        operationId = "PatchExemption",
                        tags = "exemption",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "complytId",
                                        description = "The unique identifier for an exemption (UUID)",
                                        required = true,
                                        schema = @Schema(type = "string", format = "uuid"),
                                        examples = @ExampleObject(value = PatchExemptionByComplytIdApiInfo.complytIdExample,
                                                name = PatchExemptionByComplytIdApiInfo.complytIdExample))
                        },
                        requestBody =
                        @RequestBody(
                                description = "Exemption to update",
                                required = true,
                                content = @Content(
                                        schema = @Schema(implementation = ExemptionDto.class, required = true),
                                        examples = {
                                                @ExampleObject(value = PatchExemptionByComplytIdApiInfo.patchedExemptionFieldsExample)
                                        })
                        ),
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = ExemptionDto.class),
                                                        examples = {
                                                                @ExampleObject(value = PatchExemptionByComplytIdApiInfo.returnedExemptionExample)
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
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface PatchExemptionByComplytIdApiInfo {

    String complytIdExample = "b4320a2b-1ac1-4fae-96c1-f2d7c2cc14a6";

    String patchedExemptionFieldsExample = """
            {
                "classification": {
                    "code": "code",
                    "description": "patchedDescription"
                },
                "validationDates": {
                    "fromDate": "2024-11-01T02:00:00",
                    "toDate": "2025-02-28T02:00:00"
                },
                "status": {
                    "code": "patchedCode",
                    "name": "name"
                }
            }""";

    String returnedExemptionExample = """
            [{
                     "complytId": "b4320a2b-1ac1-4fae-96c1-f2d7c2cc14a6",
                     "customerId": "85627561-bf73-45b7-ba09-8d2540a51541",
                     "state": {
                         "abbreviation": "AZ",
                         "code": "04",
                         "name": "Arizona"
                     },
                     "classification": {
                         "code": "code",
                         "description": "patchedDescription"
                     },
                     "validationDates": {
                         "fromDate": "2024-11-01T02:00:00",
                         "toDate": "2025-02-28T02:00:00"
                     },
                     "internalTimestamps": {
                         "createdDate": "2022-12-29T10:24:54.577",
                         "updatedDate": "2022-12-29T10:24:54.577"
                     },
                     "status": {
                         "code": "patchedCode",
                         "name": "name"
                     },
                     "certificate": {
                         "certificateId": "id",
                         "url": "url",
                         "name": "name"
                     },
                     "exemptionType": "FULLY",
                     "exemptionStatus": "ACTIVE"
                 }]
            """;
}
