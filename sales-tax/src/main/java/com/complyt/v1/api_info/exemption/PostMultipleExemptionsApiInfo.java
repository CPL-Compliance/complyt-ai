package com.complyt.v1.api_info.exemption;

import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import io.swagger.v3.oas.annotations.Operation;
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
                method = RequestMethod.POST,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "post multiple exemption",
                        operationId = "PostExemptions",
                        tags = "exemption",
                        requestBody =
                        @RequestBody(
                                description = "Exemption to add",
                                required = true,
                                content = @Content(
                                        schema = @Schema(implementation = ExemptionWrapperDto.class, required = true),
                                        examples = {
                                                @ExampleObject(value = PostMultipleExemptionsApiInfo.newExemptionWrapperExample)
                                        })
                        ),
                        responses = {
                                @ApiResponse(
                                        responseCode = "201",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = ExemptionDto.class),
                                                        examples = {
                                                                @ExampleObject(value = PostMultipleExemptionsApiInfo.returnedExemptionWrapperExample)
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

public @interface PostMultipleExemptionsApiInfo {

    String newExemptionWrapperExample = """
            {
                "exemption": {
                    "customerId": "f17773a9-0452-4847-9af2-548ab4acbf17",
                    "state": {
                        "abbreviation": "abbreviationPlaceHolder",
                        "code": "codePlaceHolder",
                        "name": "namePlaceHolder"
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
                    "exemptionType": "FULLY"
                },
                "states": [
                    {
                        "abbreviation": "AZ",
                        "code": "04",
                        "name": "Arizona"
                    },
                    {
                        "abbreviation": "CA",
                        "code": "04",
                        "name": "California"
                    },
                    {
                        "abbreviation": "NY",
                        "code": "04",
                        "name": "New York"
                    }
                ]
            }""";

    String returnedExemptionWrapperExample = """
            [
                 {
                     "complytId": "5df35536-a20b-42f0-b9b3-b05322a1f9b0",
                     "customerId": "f17773a9-0452-4847-9af2-548ab4acbf17",
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
                         "fromDate": "2022-11-01T02:00",
                         "toDate": "2023-02-28T02:00"
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
                     "exemptionType": "FULLY"
                 },
                 {
                     "complytId": "eedb938f-b17b-4ac2-b43d-c0d888ec6df2",
                     "customerId": "f17773a9-0452-4847-9af2-548ab4acbf17",
                     "state": {
                         "abbreviation": "CA",
                         "code": "04",
                         "name": "California"
                     },
                     "classification": {
                         "code": "code",
                         "description": "description"
                     },
                     "validationDates": {
                         "fromDate": "2022-11-01T02:00",
                         "toDate": "2023-02-28T02:00"
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
                     "exemptionType": "FULLY"
                 },
                 {
                     "complytId": "9b8af56b-9c4e-4fae-8187-7327c815278c",
                     "customerId": "f17773a9-0452-4847-9af2-548ab4acbf17",
                     "state": {
                         "abbreviation": "NY",
                         "code": "04",
                         "name": "New York"
                     },
                     "classification": {
                         "code": "code",
                         "description": "description"
                     },
                     "validationDates": {
                         "fromDate": "2022-11-01T02:00",
                         "toDate": "2023-02-28T02:00"
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
                     "exemptionType": "FULLY"
                 }
             ]""";
}