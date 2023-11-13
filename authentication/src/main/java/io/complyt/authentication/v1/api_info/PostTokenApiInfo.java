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
                                description = "Transaction to add",
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
                "clientId": "46fd75cb-bb54-4f4d-892e-1368776f4750",
                "clientSecret": "028bde0d-cdba-4009-a9df-d5b6f9fb1721"\s
            }""";

    String tokenDtoResponse = """
            {
                "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjkzMzA4MTc4LCJleHAiOjE2OTMzOTQ1NzgsImF6cCI6IjhmbEJnMXdjam5oWG5BVUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWxldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1wdGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.Q-oMSzMnumCBAzj8Y2vV2OE3Mma0gCrLgSDqq1wAEmY7riBiWoTNukWMZHAnpL8Ig7jcbaSrP5hL1n1TwWfZmfkcmLtd0vnsb9Ss-F8XZ9Hl4FmJCvdh7BQEYwxjhFmtOIGbDNze-rLYYpxcYmYSqZJxJx9cuJ7SYXk2Qa1c9EDxTNskX6tjTFK8vT7V6RIrRUg7a5fLrUmWbGT0aV--8XhcZqvDKMMwQ1lXU9Mh0gcQZ7axgCAQ0zJkg09YHYisvDaIkpUMfIfdJrz2Oh200EAYbxOV9tdn6j_oEFHbalgnWn2wcMDOfSoDERIXbkRqKkU_IKqzor-UfAILB5cGfQ",
                "scope": "create:customer delete:customer read:customer update:customer create:transaction read:transaction update:transaction delete:transaction read:state create:exemption update:exemption delete:exemption read:exemption create:nexus read:nexus delete:nexus update:nexus read:link read:sales_tax_rates",
                "expiresIn": 86400,
                "tokenType": "Bearer",
                "expireAt": "2023-08-30T11:22:48.411272"
            }""";
}