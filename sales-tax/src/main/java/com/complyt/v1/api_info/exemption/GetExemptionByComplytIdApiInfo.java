package com.complyt.v1.api_info.exemption;

import com.complyt.v1.models.customer.exemption.ExemptionDto;
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
                        description = "Get Exemption by Complyt ID",
                        operationId = "getExemptionByComplytId",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "complytId",
                                        description = "Exemption complyt ID",
                                        examples = @ExampleObject(value = GetExemptionByComplytIdApiInfo.complytIdExample,
                                                name = GetExemptionByComplytIdApiInfo.complytIdExample))
                        },
                        tags = "exemption",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = ExemptionDto.class),
                                                        examples = {
                                                                @ExampleObject(value = GetExemptionByComplytIdApiInfo.exemptionExample)
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
                                        description = "Exemption Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface GetExemptionByComplytIdApiInfo {

    String complytIdExample = "9f8ee193-1a71-42b4-801d-ee1d8a161fbe";
    String exemptionExample = """
            {
                     "complytId": "f2cfcad9-d4e2-4ade-96b4-e83b7d402933",
                     "customerId": "85627561-bf73-45b7-ba09-8d2540a51541",
                     "state": {
                         "abbreviation": "AZ",
                         "code": "04",
                         "name": "Arizona"
                     },
                     "classification": {
                         "code": "code",
                         "description": "description"
                     },
                     "validationDates": {
                         "fromDate": "2022-11-01T02:00:00",
                         "toDate": "2023-02-28T02:00:00"
                     },
                     "internalTimestamps": {
                         "createdDate": "2022-12-29T10:24:54.577",
                         "updatedDate": "2022-12-29T10:24:54.577"
                     },
                     "status": {
                         "code": "code",
                         "name": "name"
                     },
                     "certificate": {
                         "certificateId": "id",
                         "url": "url",
                         "name": "name"
                     },
                     "exemptionType": "FULLY",
                     "exemptionStatus": "ACTIVE"
                     
                 }
            """;
}
