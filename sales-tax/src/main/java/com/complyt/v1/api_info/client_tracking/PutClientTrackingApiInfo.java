package com.complyt.v1.api_info.client_tracking;

import com.complyt.v1.models.ClientTrackingDto;
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
                method = RequestMethod.PUT,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "put clientTracking",
                        operationId = "PutClientTracking",
                        tags = "clientTracking",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "tenantId",
                                        description = "The unique identifier for a customer (UUID)",
                                        required = true,
                                        schema = @Schema(type = "string", format = "uuid"),
                                        examples = @ExampleObject(value = PutClientTrackingApiInfo.tenantId,
                                                name = PutClientTrackingApiInfo.tenantId))
                        },
                        requestBody =
                        @RequestBody(
                                description = "ClientTracking to update",
                                required = true,
                                content = @Content(
                                        schema = @Schema(implementation = ClientTrackingDto.class, required = true),
                                        examples = {
                                                @ExampleObject(value = PutClientTrackingApiInfo.clientTrackingPayloadExample)
                                        })
                        ),
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = ClientTrackingDto.class),
                                                        examples = {
                                                                @ExampleObject(value = PutClientTrackingApiInfo.returnedClientTrackingExample)
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

public @interface PutClientTrackingApiInfo {
    String tenantId = "org_nD6T71fMDbR0qTSY";

    String clientTrackingPayloadExample = """
            { 
                "nexus": {
                    "taxableDate": "2015-06-01T00:00:00"
                },
                "name": "SKY",
                "tenantId": "org_nD6T71fMDbR0qTSY"
            }""";

    String returnedClientTrackingExample = """
            {
                "nexus": {
                    "taxableDate": "2015-06-01T00:00:00"
                },
                "name": "SKY",
                "internalTimestamps": {
                    "createdDate": "2024-02-08T12:31:08.316277",
                    "updatedDate": "2024-02-08T12:31:08.316277"
                },
                "tenantId": "org_nD6T71fMDbR0qTSY"
            }""";
}
