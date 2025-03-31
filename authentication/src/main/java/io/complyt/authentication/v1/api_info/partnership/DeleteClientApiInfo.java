package io.complyt.authentication.v1.api_info.partnership;

import io.complyt.authentication.v1.models.PartnershipDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
                method = RequestMethod.DELETE,
                operation =
                @Operation(
                        description = "Delete client by client's tenantId",
                        operationId = "delete",
                        tags = "partnership",
                        parameters = {
                                @Parameter(
                                        name = "tenantId",
                                        description = "The tenantId of the client",
                                        required = true,
                                        example = "org_111111111"
                                )
                        },
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = PartnershipDto.class),
                                                        examples = {
                                                                @ExampleObject(value = DeleteClientApiInfo.partnershipDtoResponse)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "404",
                                        description = "Requested client could not be found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface DeleteClientApiInfo {
    String partnershipDtoResponse = """
        {
            "tenantId": "org_1234512345",
            "partnerName": "PartnerCorp",
            "supportedReferrals": [
                {
                    "tenantId": "org_123456789",
                    "name": "client name #1",
                    "partnershipStatus": "ACTIVE",
                    "timestamps": {
                            "createdDate": "2024-01-01T00:00:00",
                            "updatedDate": "2024-01-01T00:00:00"
                            }
                },
                {
                    "tenantId": "org_111111111",
                    "name": "client name #2",
                    "partnershipStatus": "CANCELLED",
                    "timestamps": {
                            "createdDate": "2024-01-01T00:00:00",
                            "updatedDate": "2024-01-01T00:00:00"
                            }
                }
            ]
        }""";

}