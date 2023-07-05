package integration.test_utils;

public class TestUtilities {

    public static final String TRANSACTION_BASE_URL = "/v1/transactions";
    public static final String CUSTOMER_BASE_URL = "/v1/customers";
    public static final String SALES_TAX_TRACKING_BASE_URL = "/v1/nexus";
    public static final String SALES_TAX_RATES_BASE_URL = "/v1/sales_tax_rates";
    public static final String FILES_BASE_URL = "/v1/files";
    public static final String NON_EXISTING_COMPLYT_ID = "11111111-1111-1111-1111-111111111111";

    public static String unvalidatedSalesTaxTrackingJsonExample(String stateName, String stateAbbreviation) {
        return salesTaxTrackingJsonExample(stateName, stateAbbreviation, null, false);
    }

    public static String salesTaxTrackingJsonExample(String stateName, String stateAbbreviation, String complytId) {
        return salesTaxTrackingJsonExample(stateName, stateAbbreviation, complytId, true);
    }


    private static String salesTaxTrackingJsonExample(String stateName, String stateAbbreviation, String complytId, boolean isValidated) {
        return String.format("""
                        {
                             %s
                             "state": {
                                 "abbreviation": "%s",
                                 %s
                                 "name": "%s"
                             },
                             "enforcesSalesTax": true,
                             "physicalNexusTracker": {
                                 %s
                                 "established": false
                             },
                             "economicNexusTracker": {
                                 "established": true,
                                 "establishedDate": "2022-08-02T16:12:00"
                             },
                             "appliedDate": "2015-08-02T16:12:00",
                             "approvalDate": "2015-06-22T13:57:00",
                             "approved": true
                         }
                         """,
                complytId != null ? "\"complytId\": \"" + complytId + "\"," : "",
                stateAbbreviation,
                isValidated ? "\"code\": \"06\"," : "",
                stateName,
                isValidated ? "\"establishedDate\": \"2000-12-31T22:00:00\"," : ""
        );
    }

    public static String customerJsonExample(String externalId, String complytId) {
        return customerJsonExample(externalId, complytId, true);
    }

    public static String unvalidatedCustomerJsonExample(String externalId, String complytId) {
        return customerJsonExample(externalId, complytId, false);
    }

    private static String customerJsonExample(String externalId, String complytId, boolean isValidated) {
        return String.format("""
                        {
                            %s
                            "externalId": "%s",
                            %s
                            "name": "Complyt",
                            "address": {
                                "city": "Sacramento",
                                "country": "US",
                                "county": null,
                                "state": "CA",
                                "street": "944 W. Wintergreen St.",
                                "zip": "95823"
                            },
                            %s
                            "internalTimestamps": {
                                "createdDate": "2023-01-10T17:40:44.357",
                                "updatedDate": "2023-01-11T17:10:21.275"
                            },
                            "externalTimestamps": {
                                "createdDate": "2022-10-19T07:00:00",
                                "updatedDate": "2022-10-19T09:07:54.585"
                            }
                        }""",
                complytId != null ? "\"complytId\": \"" + complytId + "\"," : "",
                externalId,
                isValidated ? "\"source\": 1," : "",
                isValidated ? "\"customerType\": \"RETAIL\"," : "");
    }

    public static String unvalidatedTransactionJsonExample(String externalId, String customerId) {
        return transactionJsonExample(externalId, customerId, null, false, null);
    }

    public static String transactionJsonExample(String externalId, String customerId) {
        return transactionJsonExample(externalId, customerId, null, true, null);
    }

    public static String existingTransactionJsonExample(String externalId, String customerId, String complytId) {
        return transactionJsonExample(externalId, customerId, complytId, true, null);
    }

    public static String transactionJsonExampleWithState(String externalId, String customerId, String state) {
        return transactionJsonExample(externalId, customerId, null, true, state);
    }

    private static String transactionJsonExample(String externalId, String customerId, String complytId, boolean isValidated, String state) {
        return String.format("""
                        {
                            %s
                            "externalId": "%s",
                            %s
                            "items": [
                                {
                                    "unitPrice": 0,
                                    "quantity": 0,
                                    "totalPrice": 0,
                                    "description": "string",
                                    "name": "string",
                                    "taxCode": "C1S1",
                                    "manualSalesTax": true,
                                    "manualSalesTaxRate": 0
                                }
                            ],
                            "shippingAddress": {
                                "city": "Los Angeles",
                                "country": "US",
                                "state": "%s",
                                "street": "10 5th Ave",
                                "zip": "90210"
                            },
                            "customerId": "%s",
                            "transactionStatus": "ACTIVE",
                            "externalTimestamps": {
                                "createdDate": "2023-02-05T12:24:43.193Z",
                                "updatedDate": "2023-02-05T12:24:43.193Z"
                            },
                            %s
                            "shippingFee": {
                                "manualSalesTax": true,
                                "manualSalesTaxRate": 0,
                                "totalPrice": 0,
                                "taxCode": "C6S1"
                            }
                        }
                        """,
                complytId != null ? "\"complytId\": \"" + complytId + "\"," : "",
                externalId,
                isValidated ? "\"source\": \"1\"," : "",
                state != null ? state : "CA",
                customerId,
                isValidated ? "\"transactionType\": \"INVOICE\"," : ""
        );
    }
}
