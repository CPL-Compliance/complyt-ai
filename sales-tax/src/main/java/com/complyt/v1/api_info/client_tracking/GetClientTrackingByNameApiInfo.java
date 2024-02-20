package com.complyt.v1.api_info.client_tracking;

import com.complyt.v1.models.ClientTrackingDto;
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
                        description = "Get ClientTracing by Name",
                        operationId = "getClientTracingByName",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "name",
                                        description = "name of the clientTracking",
                                        examples = @ExampleObject(name = GetClientTrackingByNameApiInfo.name,
                                        value =  GetClientTrackingByNameApiInfo.name))
                        },
                        tags = "clientTracing",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = ClientTrackingDto.class),
                                                        examples = {
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
                                        description = "ClientTracing Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})

public @interface GetClientTrackingByNameApiInfo {
    String name = "Raz";

    String clientTrackingName = """
            [{
                "complytId": "9f8ee193-1a71-42b4-801d-ee1d8a161fbe",
                "externalId": "externalIdExample",
                "source": "sourceExample",
                "name": "Raz",
                "nexus": {
                    "taxableDate": "2015-06-01T00:00:00"
                },
                "tenantId": "org_nD6T71fMDbR0qTSY",
                "internalTimestamps": {
                    "createdDate": "2023-01-10T17:40:44.357",
                    "updatedDate": "2023-01-11T17:10:21.275"
                }
            }]""";
}
