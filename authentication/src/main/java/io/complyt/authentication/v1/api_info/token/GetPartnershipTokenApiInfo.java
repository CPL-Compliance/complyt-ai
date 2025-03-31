package io.complyt.authentication.v1.api_info.token;

import io.complyt.authentication.v1.models.TokenDto;
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
                method = RequestMethod.GET,
                operation =
                @Operation(
                        description = "Get access token by client's tenantId",
                        operationId = "get",
                        tags = "token",
                        parameters = {
                                @Parameter(
                                        name = "tenantId",
                                        description = "The tenantId of the client",
                                        required = true,
                                        example = "org_123456789"
                                )
                        },
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = TokenDto.class),
                                                        examples = {
                                                                @ExampleObject(value = GetPartnershipTokenApiInfo.tokenDtoResponse)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "401",
                                        description = "Client tenantId could not be found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface GetPartnershipTokenApiInfo {
    String tokenDtoResponse = """
            {
                "accessToken": "stub_access_token",
                "scope": "read:stub create:stub delete:stub update:stub",
                "expiresIn": 86400,
                "tokenType": "Bearer",
                "expireAt": "2023-08-30T11:22:48.411272"
            }""";

}