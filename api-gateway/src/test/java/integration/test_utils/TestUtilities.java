package integration.test_utils;

import java.math.BigDecimal;
import java.util.Arrays;

public class TestUtilities {

    public static final String TRANSACTION_BASE_URL = "/v1/transactions";
    public static final String CUSTOMER_BASE_URL = "/v1/customers";
    public static final String CLIENT_TRACKING_BASE_URL = "/v1/clientTracking";
    public static final String SALES_TAX_TRACKING_BASE_URL = "/v1/nexus";
    public static final String SALES_TAX_RATES_BASE_URL = "/v1/sales_tax_rates";
    public static final String FILES_BASE_URL = "/v1/files";
    public static final String TOKEN_BASE_URL = "/v1/token";
    public static final String API_KEY_BASE_URL = "/v1/api_key";
    public static final String SECRET_KEY_BASE_URL = "/v1/secret_key";
    public static final String NON_EXISTING_COMPLYT_ID = "d18068f0-6d98-4b0d-ba19-4536f0b4173a";

    /*
    We have 5 sets of API_KEY_CLIENT_ID, API_KEY_CLIENT_SECRET.
    The API_KEY_CLIENT_ID, API_KEY_CLIENT_SECRET are in use for all the non-changing tests (unit and integration).
    The other 4 sets API_KEY_CLIENT_ID#, API_KEY_CLIENT_SECRET# are being used in cases where the test might mutate
    the document (DELETE API Key) or when we want to test a few times the behavior of a metric that happens for
    the first time (POST Token).
     */
    public static final String API_KEY_CLIENT_ID = "78fd4034-53af-4144-b2da-27ac31cdf45c";
    public static final String API_KEY_CLIENT_SECRET = "3d446591-d839-4906-97fe-85e1b51df0c8";
    public static final String API_KEY_CLIENT_ID1 = "0f6daf12-5851-4fa9-8f6c-4e3af2e28922";
    public static final String API_KEY_CLIENT_SECRET1 = "c0faf9b1-1539-454c-b87e-34b6d39fdc54";
    public static final String API_KEY_CLIENT_ID2 = "a4e25dac-e414-4491-bdd2-552c83939ec5";
    public static final String API_KEY_CLIENT_SECRET2 = "0e60bc95-5876-4036-9c32-c23bec5f045b";
    public static final String API_KEY_CLIENT_ID3 = "70b4d5d7-c0cd-469f-a8c4-02383f57f79a";
    public static final String API_KEY_CLIENT_SECRET3 = "06795f62-027a-439a-8983-33e7f33e8d78";
    public static final String API_KEY_CLIENT_ID4 = "35b0163d-6161-451b-afda-d4f5476cd81a";
    public static final String API_KEY_CLIENT_SECRET4 = "27efbba1-2a52-4561-a7c9-ddc54c166331";

    public static final String NULL_STRING = "null";

    public static String unvalidatedSalesTaxTrackingJsonExample(String stateName, String stateAbbreviation) {
        return salesTaxTrackingJsonExample(stateName, stateAbbreviation, null, false);
    }

    public static String salesTaxTrackingJsonExample(String stateName, String stateAbbreviation, String complytId) {
        return salesTaxTrackingJsonExample(stateName, stateAbbreviation, complytId, true);
    }

    public static String clientTrackingJsonExample(String name, String tenantId) {
            return "{\n" +
                    "    \"nexus\": {\n" +
                    "        \"taxableDate\": \"2015-06-01T00:00:00\"\n" +
                    "    },\n" +
                    "    \"name\": \"" + name + "\",\n" +
                    "    \"tenantId\": \"" + tenantId + "\"\n" +
                    "}";
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
                             "comment": "a comment",
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
                            "email": "shtak@dope.com",
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

    public static String unvalidatedTransactionJsonExample(String externalId, String customerId, String createdDate) {
        return transactionJsonExample(externalId, customerId, null, false, null, createdDate);
    }

    public static String transactionJsonExample(String externalId, String customerId, String state, String createdDate) {
        return transactionJsonExample(externalId, customerId, null, true, state, createdDate);
    }

    public static String existingTransactionJsonExample(String externalId, String customerId, String complytId, String createdDate) {
        return transactionJsonExample(externalId, customerId, complytId, true, null, createdDate);
    }

    public static String transactionJsonExampleWithState(String externalId, String customerId, String state, String createdDate) {
        return transactionJsonExample(externalId, customerId, null, true, state, createdDate);
    }

    private static String transactionJsonExample(String externalId, String customerId, String complytId, boolean isValidated, String state, String createdDate) {
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
                                "zip": "90210",
                                "isPartial": "false"
                            },
                            "customerId": "%s",
                            "transactionStatus": "ACTIVE",
                            "externalTimestamps": {
                                "createdDate": "%sT12:24:43.193Z",
                                "updatedDate": "%sT12:24:43.193Z"
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
                state,
                customerId,
                createdDate,
                createdDate,
                isValidated ? "\"transactionType\": \"INVOICE\"," : ""
        );
    }

    public static String getClientCredentialsJsonExample() {
        return "{\n" +
                "    \"clientId\": \"abc\",\n" +
                "    \"clientSecret\": \"QWE$#@\"\n" +
                "}";
    }

    public static String getNonExistingClientCredentialsJsonExample() {
        return "{\n" +
                "    \"clientId\": \"rte\",\n" +
                "    \"clientSecret\": \"QWE$#@\"\n" +
                "}";
    }

    public static String apiKeyJsonExample() {
        return "{\n" +
                "    \"clientId\":\"" + API_KEY_CLIENT_ID + "\",\n" +
                "    \"clientSecret\":\"" + API_KEY_CLIENT_SECRET + "\"\n" +
                "}";
    }

    public static String apiKey2JsonExample() {
        return "{\n" +
                "    \"clientId\":\"" + API_KEY_CLIENT_ID2 + "\",\n" +
                "    \"clientSecret\":\"" + API_KEY_CLIENT_SECRET2 + "\"\n" +
                "}";
    }
    public static String apiKey4JsonExample() {
        return "{\n" +
                "    \"clientId\":\"" + API_KEY_CLIENT_ID4 + "\",\n" +
                "    \"clientSecret\":\"" + API_KEY_CLIENT_SECRET4 + "\"\n" +
                "}";
    }

    public static String apiKeyUrlEncodedExample() {
        return "clientId=" + API_KEY_CLIENT_ID +
                "&clientSecret=" + API_KEY_CLIENT_SECRET;
    }
    public static String apiKey1UrlEncodedExample() {
        return "clientId=" + API_KEY_CLIENT_ID1 +
                "&clientSecret=" + API_KEY_CLIENT_SECRET1;
    }

    public static String apiKey3UrlEncodedExample() {
        return "clientId=" + API_KEY_CLIENT_ID3 +
                "&clientSecret=" + API_KEY_CLIENT_SECRET3;
    }

    public static String transactionItemIsNotAligned(String externalId, String customerId, String complytId, boolean isValidated, String state) {
        return String.format("""
                        {
                            %s
                            "externalId": "%s",
                            %s
                            "items": [
                                {
                                    "unitPrice": 1000,
                                    "quantity": 1,
                                    "totalPrice": -1000,
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
                                "zip": "90210",
                                "isPartial": "false"
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

    public static String transactionTotalIsNegative(String externalId, String customerId, String complytId, boolean isValidated, String state) {
        return String.format("""
                        {
                            %s
                            "externalId": "%s",
                            %s
                            "items": [
                                {
                                    "unitPrice": 1000,
                                    "quantity": 1,
                                    "totalPrice": 1000,
                                    "description": "string",
                                    "name": "string",
                                    "taxCode": "C1S1",
                                    "manualSalesTax": true,
                                    "manualSalesTaxRate": 0
                                },
                                {
                                    "unitPrice": -1100,
                                    "quantity": 1,
                                    "totalPrice": -1100,
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
                                "zip": "90210",
                                "isPartial": "false"
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

    public static String customItem(BigDecimal total, BigDecimal quantity, BigDecimal unitPrice, BigDecimal discount) {
        return String.format("""
                        {
                                            "unitPrice": %s,
                                            "quantity": %s,
                                            "totalPrice": %s,
                                            "description": "string",
                                            "name": "string",
                                            "taxCode": "C1S1",
                                            "manualSalesTax": true,
                                            "manualSalesTaxRate": 0,
                                            "discount":%s
                                        }
                        """,
                unitPrice,
                quantity,
                total,
                discount);
    }

    public static String transactionWithCustomItems(String externalId, String customerId, String complytId, boolean isValidated, String state,
                                                    String... items) {
        return String.format("""
                        {
                            %s
                            "externalId": "%s",
                            %s
                            "items": %s,
                            "shippingAddress": {
                                "city": "Los Angeles",
                                "country": "US",
                                "state": "%s",
                                "street": "10 5th Ave",
                                "zip": "90210",
                                "isPartial": "false"
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
                Arrays.toString(items),
                state != null ? state : "CA",
                customerId,
                isValidated ? "\"transactionType\": \"INVOICE\"," : ""
        );
    }

    public static String stringWithLength(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (; 0 < length; length--) stringBuilder.append('a');
        return stringBuilder.toString();
    }
}
