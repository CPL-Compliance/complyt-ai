package com.complyt.v1.api_info.customer;

import com.complyt.v1.models.customer.CustomerDto;
import io.swagger.v3.oas.annotations.Operation;
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
                        description = "Get all Customers",
                        operationId = "getAllCustomers",
                        tags = "customer",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = CustomerDto.class),
                                                        examples = {
                                                                @ExampleObject(value = GetAllCustomersApiInfo.customerExample)
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
public @interface GetAllCustomersApiInfo {
    String customerExample = """
            [{
                "complytId": "9f8ee193-1a71-42b4-801d-ee1d8a161fbe",
                "externalId": "999444",
                "source": "1",
                "name": "Complyt",
                "address": {
                    "city": "Sacramento",
                    "country": "US",
                    "county": null,
                    "state": "CA",
                    "street": "944 W. Wintergreen St.",
                    "zip": "95823",
                    "isPartial": false
                },
                "customerType": "RETAIL",
                "internalTimestamps": {
                    "createdDate": "2023-01-10T17:40:44.357",
                    "updatedDate": "2023-01-11T17:10:21.275"
                },
                "externalTimestamps": {
                    "createdDate": "2022-10-19T07:00:00",
                    "updatedDate": "2022-10-19T09:07:54.585"
                }
            }]""";
}