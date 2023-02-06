package com.complyt.v1.api_info.sales_tax_tracking;

import com.complyt.v1.models.SalesTaxTrackingDto;
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
                        description = "Upsert Sales tax tracking by state",
                        operationId = "upsertSalesTaxTrackingByState",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "state",
                                        description = "State",
                                        examples = @ExampleObject(value = UpsertSalesTaxTrackingByStateApiInfo.stateExample,
                                                name = UpsertSalesTaxTrackingByStateApiInfo.stateExample))
                        },
                        tags = "salesTaxTracking",
                        requestBody =
                        @RequestBody(
                                description = "Sales tax tracking to add",
                                required = true,
                                content = @Content(
                                        schema = @Schema(implementation = SalesTaxTrackingDto.class, required = true),
                                        examples = {
                                                @ExampleObject(value = UpsertSalesTaxTrackingByStateApiInfo.newSalesTaxTrackingsExample)
                                        })
                        ),
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = SalesTaxTrackingDto.class),
                                                        examples = {
                                                                @ExampleObject(value = UpsertSalesTaxTrackingByStateApiInfo.returnedSalesTaxTrackingsExample)
                                                        })
                                        }),
                                @ApiResponse(
                                        responseCode = "201",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = SalesTaxTrackingDto.class),
                                                        examples = {
                                                                @ExampleObject(value = UpsertSalesTaxTrackingByStateApiInfo.returnedSalesTaxTrackingsExample)
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
public @interface UpsertSalesTaxTrackingByStateApiInfo {

    String stateExample = "California";

    String newSalesTaxTrackingsExample = """
            {
                {
                    "state": {
                        "abbreviation": "CA",
                        "code": "06",
                        "name": "California"
                    },
                    "enforcesSalesTax": true,
                    "physicalNexusTracker": {
                        "established": false,
                        "establishedDate": "2000-12-31T22:00:00"
                    },
                    "economicNexusTracker": {
                        "established": true,
                        "establishedDate": "2022-08-02T16:12:00"
                    },
                    "appliedDate": "2015-08-02T16:12:00",
                    "approved": true,
                    "approvalDate": "2015-06-22T13:57:00"
                }
            }
            """;

    String returnedSalesTaxTrackingsExample = """
            {
                {
                    "complytId": "679cab51-7d88-41a6-b587-3eceecdd9524",
                    "state": {
                        "abbreviation": "CA",
                        "code": "06",
                        "name": "California"
                    },
                    "enforcesSalesTax": true,
                    "physicalNexusTracker": {
                        "established": false,
                        "establishedDate": "2000-12-31T22:00:00"
                    },
                    "economicNexusTracker": {
                        "established": true,
                        "establishedDate": "2022-08-02T16:12:00"
                    },
                    "appliedDate": "2015-08-02T16:12:00",
                    "approved": true,
                    "approvalDate": "2015-06-22T13:57:00"
                }
            }
            """;
}
