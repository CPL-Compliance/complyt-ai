package com.complyt.v1.api_info.exemption;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                method = RequestMethod.GET,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Delete Exemption by Complyt ID",
                        operationId = "deleteExemptionByComplytId",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "complytId",
                                        description = "Exemption complyt ID",
                                        examples = @ExampleObject(value = DeleteExemptionByComplytIdApiInfo.complytIdExample,
                                                name = DeleteExemptionByComplytIdApiInfo.complytIdExample))
                        },
                        tags = "exemption",
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
                                        responseCode = "404",
                                        description = "Exemption Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface DeleteExemptionByComplytIdApiInfo {

    String complytIdExample = "9f8ee193-1a71-42b4-801d-ee1d8a161fbe";
}
