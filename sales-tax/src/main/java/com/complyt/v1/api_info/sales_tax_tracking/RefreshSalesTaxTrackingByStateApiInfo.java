package com.complyt.v1.api_info.sales_tax_tracking;

import com.complyt.v1.models.SalesTaxTrackingDto;
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
                method = RequestMethod.POST,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Refresh Sales tax tracking by state and date",
                        operationId = "refreshSalesTaxTrackingByState",
                        parameters = {
                                @Parameter(in = ParameterIn.QUERY,
                                        name = "date",
                                        description = "a date to refresh by",
                                        examples = @ExampleObject(value = RefreshSalesTaxTrackingByStateApiInfo.dateExample,
                                                name = RefreshSalesTaxTrackingByStateApiInfo.dateExample)),
                                @Parameter(in = ParameterIn.PATH,
                                        name = "state",
                                        description = "State",
                                        examples = @ExampleObject(value = RefreshSalesTaxTrackingByStateApiInfo.stateExample,
                                                name = RefreshSalesTaxTrackingByStateApiInfo.stateExample))
                        },
                        tags = "salesTaxTracking",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = SalesTaxTrackingDto.class),
                                                        examples = {
                                                                @ExampleObject(value = RefreshSalesTaxTrackingByStateApiInfo.returnedSalesTaxTrackingsExample)
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
public @interface RefreshSalesTaxTrackingByStateApiInfo {

    String stateExample = "California";

    String dateExample = "2024-01-01";

    String returnedSalesTaxTrackingsExample = """
            {
                 "complytId": "679cab51-7d88-41a6-b587-3eceecdd9524",
                 "comment": "this is a nexus tracking",
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
                 "nexusCalculationSummaries": {
                         "2024-01-01": {
                             "count": 338,
                             "amount": 256800.0
                         }
                     },
                     "nexusStateRule": {
                         "enforcesSalesTax": true,
                         "state": {
                             "abbreviation": "CA",
                             "code": "06",
                             "name": "California"
                         },
                         "taxableCategories": [
                             "TAXABLE"
                         ],
                         "tangibleCategories": [
                             "TANGIBLE"
                         ],
                         "customerTypes": [
                             "RETAIL",
                             "MARKETPLACE",
                             "RESELLER"
                         ],
                         "timeFrame": "CURRENT_TAXABLE_YEAR",
                         "nexusThreshold": {
                             "amount": 500000,
                             "count": 0,
                             "definition": "AMOUNT"
                         },
                         "appliedDate": "1970-01-01T00:00:00"
                     },
                     "clientTracking": {
                         "nexus": {
                             "taxableDate": "2015-01-01T00:00:00"
                         },
                         "name": "some client"
                     },
                 "appliedDate": "2015-08-02T16:12:00",
                 "approved": true,
                 "approvalDate": "2015-06-22T13:57:00",
                 "filingFrequency": "MONTHLY"
             }
            """;
}
