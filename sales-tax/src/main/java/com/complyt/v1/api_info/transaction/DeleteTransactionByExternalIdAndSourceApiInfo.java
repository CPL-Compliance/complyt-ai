package com.complyt.v1.api_info.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
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
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Get all Transactions By Source",
                        operationId = "getAllTransactionsBySource",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "source",
                                        description = "Transaction Source (should be [1-9])",
                                        required = true,
                                        schema = @Schema(type = "string", pattern = "[1-9]"),
                                        examples = @ExampleObject(value = DeleteTransactionByExternalIdAndSourceApiInfo.sourceExample,
                                                name = DeleteTransactionByExternalIdAndSourceApiInfo.sourceExample)),
                                @Parameter(in = ParameterIn.PATH,
                                        name = "externalId",
                                        description = "Transaction External Id",
                                        examples = @ExampleObject(value = DeleteTransactionByExternalIdAndSourceApiInfo.externalIdExample,
                                                name = DeleteTransactionByExternalIdAndSourceApiInfo.externalIdExample))
                        },
                        tags = "transaction",
                        responses = {
                                @ApiResponse(
                                        responseCode = "204",
                                        description = "Successful operation"
                                ),
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
public @interface DeleteTransactionByExternalIdAndSourceApiInfo {
    String sourceExample = "1";
    String externalIdExample = "999442";
}
