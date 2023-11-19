package io.complyt.authentication.v1.api_info;

import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
                method = RequestMethod.POST,
                operation =
                @Operation(
                        description = "Get access token by API key",
                        operationId = "post",
                        tags = "token",
                        requestBody =
                        @RequestBody(
                                description = "Api-Key parameters",
                                required = true,
                                content = @Content(
                                        schema = @Schema(implementation = ApiKeyDto.class, required = true),
                                        examples = {
                                                @ExampleObject(value = io.complyt.authentication.v1.api_info
                                                        .PostTokenApiInfo.apiKeyBody)
                                        })
                        ),
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = TokenDto.class),
                                                        examples = {
                                                                @ExampleObject(value = PostTokenApiInfo.tokenDtoResponse)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "404",
                                        description = "Api Key Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface PostTokenApiInfo {

    String apiKeyBody = """
            {
                "clientId": "bce87cba-1756-417f-942c-23930e9f7b1c",
                "clientSecret": "b4332d97-97a4-46d8-887a-0e8b4bea161b"\s
            }""";

    String tokenDtoResponse = """
            {
                "accessToken": "stub_access_token",
                "scope": "read:stub create:stub delete:stub update:stub",
                "expiresIn": 86400,
                "tokenType": "Bearer",
                "expireAt": "2023-08-30T11:22:48.411272"
            }""";

}