package com.complyt.v1.customer;

import com.complyt.v1.models.customer.CustomerDto;
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
                        description = "Update or Insert Customer by External ID",
                        operationId = "upsertCustomerByExternalId",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "externalId",
                                        description = "Customer External ID",
                                        examples = @ExampleObject(value = UpsertCustomeByExternalIdApiInfo.externalIdExample,
                                                name = UpsertCustomeByExternalIdApiInfo.externalIdExample))
                        },
                        tags = "customer",
                        requestBody =
                        @RequestBody(
                                description = "Customer to add",
                                required = true,
                                content = @Content(
                                        schema = @Schema(implementation = CustomerDto.class, required = true),
                                        examples = {
                                                @ExampleObject(value = UpsertCustomeByExternalIdApiInfo.newCustomerExample)
                                        })
                        ),
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = CustomerDto.class),
                                                        examples = {
                                                                @ExampleObject(value = UpsertCustomeByExternalIdApiInfo.returnedCustomerExample)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "400",
                                        description = "Something is wrong with your request"
                                ),
                                @ApiResponse(
                                        responseCode = "404",
                                        description = "Customer Not Found"),
                                @ApiResponse(
                                        responseCode = "500",
                                        description = "Internal Error"
                                )
                        }))
})
public @interface UpsertCustomeByExternalIdApiInfo {
    String externalIdExample = "999444";
    String newCustomerExample = "{\n" +
            "    \"externalId\":" + externalIdExample + ",\n" +
            "    \"name\": \"Complyt LTD.\",\n" +
            "    \"address\": {\n" +
            "        \"city\": \"Sacramento\",\n" +
            "        \"country\": \"US\",\n" +
            "        \"county\": null,\n" +
            "        \"state\": \"CA\",\n" +
            "        \"street\": \"944 W. Wintergreen St.\",\n" +
            "        \"zip\": \"95823\"\n" +
            "    },\n" +
            "    \"customerType\": \"RETAIL\",\n" +
            "    \"externalTimestamps\": {\n" +
            "        \"createdDate\": \"2022-10-19T07:00:00.000Z\",\n" +
            "        \"updatedDate\": \"2022-10-19T09:07:54.585Z\"\n" +
            "    },\n" +
            "    \"internalTimestamps\": {\n" +
            "        \"createdDate\": \"2022-10-19T07:00:00.000Z\",\n" +
            "        \"updatedDate\": \"2022-10-19T09:07:54.585Z\"\n" +
            "    }\n" +
            "}";

    String returnedCustomerExample = "{\n" +
            "  \"id\": \"63bd86fd9c005a684b5fd2f0\",\n" +
            "  \"externalId\":" + externalIdExample + ",\n" +
            "  \"name\": \"Complyt LTD.\",\n" +
            "  \"address\": {\n" +
            "    \"city\": \"Sacramento\",\n" +
            "    \"country\": \"US\",\n" +
            "    \"county\": null,\n" +
            "    \"state\": \"CA\",\n" +
            "    \"street\": \"944 W. Wintergreen St.\",\n" +
            "    \"zip\": \"95823\"\n" +
            "  },\n" +
            "  \"customerType\": \"RETAIL\",\n" +
            "  \"internalTimestamps\": {\n" +
            "    \"createdDate\": {\n" +
            "      \"timestamp\": \"2023-01-10T17:40:44.357271\"\n" +
            "    },\n" +
            "    \"updatedDate\": {\n" +
            "      \"timestamp\": \"2023-01-10T17:40:44.357271\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"externalTimestamps\": {\n" +
            "    \"createdDate\": {\n" +
            "      \"timestamp\": \"2022-10-19T07:00:00\"\n" +
            "    },\n" +
            "    \"updatedDate\": {\n" +
            "      \"timestamp\": \"2022-10-19T09:07:54.585\"\n" +
            "    }\n" +
            "  }\n" +
            "}";
}