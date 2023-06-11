package com.complyt.v1.api_info.transaction;

import com.complyt.v1.models.TransactionDto;
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
                method = RequestMethod.GET,
                operation =
                @Operation(
                        security = @SecurityRequirement(name = "bearerAuth"),
                        description = "Get all Transactions By Source",
                        operationId = "getAllTransactionsBySource",
                        parameters = {
                                @Parameter(in = ParameterIn.PATH,
                                        name = "source",
                                        description = "Transaction Source",
                                        examples = @ExampleObject(value = GetAllTransactionsBySourceApiInfo.sourceExample,
                                                name = GetAllTransactionsBySourceApiInfo.sourceExample))
                        },
                        tags = "transaction",
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Successful operation",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = TransactionDto.class),
                                                        examples = {
                                                                @ExampleObject(value = GetAllTransactionsBySourceApiInfo.transactionsExample)
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
public @interface GetAllTransactionsBySourceApiInfo {

    String sourceExample = "1";
    String transactionsExample = """
            [
                        {
                            "complytId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                            "externalId": "999444",
                            "source": "1",
                            "items": [
                                {
                                    "unitPrice": 0,
                                    "quantity": 0,
                                    "totalPrice": 0,
                                    "description": "string",
                                    "name": "string",
                                    "taxCode": "string",
                                    "jurisdictionalSalesTaxRules": {
                                        "name": "string",
                                        "abbreviation": "string",
                                        "taxable": true,
                                        "specialTreatment": true,
                                        "calculationType": "FIXED",
                                        "description": "string",
                                        "calculationValue": 0,
                                        "cities": null
                                    },
                                    "salesTaxRates": {
                                        "cityDistrictRate": 0,
                                        "cityRate": 0,
                                        "countyDistrictRate": 0,
                                        "countyRate": 0,
                                        "stateRate": 0,
                                        "taxRate": 0
                                    },
                                    "manualSalesTax": true,
                                    "manualSalesTaxRate": 0,
                                    "tangibleCategory": "TANGIBLE",
                                    "taxableCategory": "TAXABLE"
                                }
                            ],
                            "billingAddress": {
                                "city": "string",
                                "country": "string",
                                "county": "string",
                                "state": "string",
                                "street": "string",
                                "zip": "string",
                                "isPartial": false
                            },
                            "shippingAddress": {
                                "city": "string",
                                "country": "string",
                                "county": "string",
                                "state": "string",
                                "street": "string",
                                "zip": "string",
                                "isPartial": false
                            },
                            "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                            "customer": {
                                "complytId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                "externalId": "string",
                                "source": "7",
                                "name": "string",
                                "address": {
                                    "city": "string",
                                    "country": "string",
                                    "county": "string",
                                    "state": "string",
                                    "street": "string",
                                    "zip": "string"
                                },
                                "customerType": "RETAIL",
                                "internalTimestamps": {
                                    "createdDate": "2023-02-05T12:24:43.193Z",
                                    "updatedDate": "2023-02-05T12:24:43.193Z"
                                },
                                "externalTimestamps": {
                                    "createdDate": "2023-02-05T12:24:43.193Z",
                                    "updatedDate": "2023-02-05T12:24:43.193Z"
                                }
                            },
                            "salesTax": {
                                "amount": 0,
                                "salesTaxRates": {
                                    "cityDistrictRate": 0,
                                    "cityRate": 0,
                                    "countyDistrictRate": 0,
                                    "countyRate": 0,
                                    "stateRate": 0,
                                    "taxRate": 0
                                }
                            },
                            "transactionStatus": "ACTIVE",
                            "internalTimestamps": {
                                "createdDate": "2023-02-05T12:24:43.193Z",
                                "updatedDate": "2023-02-05T12:24:43.193Z"
                            },
                            "externalTimestamps": {
                                "createdDate": "2023-02-05T12:24:43.193Z",
                                "updatedDate": "2023-02-05T12:24:43.193Z"
                            },
                            "transactionType": "SALES_ORDER",
                            "shippingFee": {
                                "manualSalesTax": true,
                                "manualSalesTaxRate": 0,
                                "totalPrice": 0,
                                "taxCode": "string",
                                "taxableCategory": "TAXABLE",
                                "tangibleCategory": "TANGIBLE",
                                "jurisdictionalSalesTaxRules": {
                                    "name": "string",
                                    "abbreviation": "string",
                                    "taxable": true,
                                    "specialTreatment": true,
                                    "calculationType": "FIXED",
                                    "description": "string",
                                    "calculationValue": 0,
                                    "cities": null
                                },
                                "salesTaxRates": {
                                    "cityDistrictRate": 0,
                                    "cityRate": 0,
                                    "countyDistrictRate": 0,
                                    "countyRate": 0,
                                    "stateRate": 0,
                                    "taxRate": 0
                                }
                            },
                            "createdFrom": "string",
                            "taxableItemsAmount": 0,
                            "tangibleItemsAmount": 0,
                            "totalItemsAmount": 0
                        }
            ]
            """;
}