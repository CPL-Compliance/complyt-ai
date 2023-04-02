package com.complyt.v1.api_info;

public interface FieldsDescriptions {

    /**
     * The rules for specifying descriptions for fields in our openAPI implementation are:
     * 1. if it's a single-value field (primitive types, UUID etc.), define description in the fields @Schema annotation directly.
     * 2. if it's a complex object and all objects of same class have same description, define the description in the class @Schema annotation.
     * 3. if it's an object and each instance have a different descriptions, define the description in the OpenApiConfig file
     */

    // Resources
    String CUSTOMER = "The buyer. All transactions are associated with a customer";

    String TRANSACTION = "A transaction between the API consumer and a customer";

    String EXEMPTION = "An exemption from sales tax associated with a customer";

    String SALES_TAX_TRACKING = "Tracks the organization's sales tax status in each US state";

    // General
    String COMPLYT_ID = "The unique identifier in Complyt of the "; // add Dto

    String EXTERNAL_ID = "the attribute designed to receive a unique identifier provided by API consumers themselves (e.g. pass your own ID)";

    String SOURCE = "a single digit which identifies your different stores, checkout solutions, marketplaces, etc. defined by the API consumers (to avoid external id duplications)";

    String CUSTOMER_ID = "The Complyt ID of the customer associated with the "; // add Dto

    String INTERNAL_TIMESTAMPS = "read-only. The APIs internal timestamps for this resource";

    String EXTERNAL_TIMESTAMPS = "External timestamps, which should reference the dates in your originating system since these are the timestamps used for sales tax calculation";

    String TIMESTAMP_FORMAT = "valid ISO8601 format. " +
            "Supported formats are 'YYYY-MM-DD'/ 'YYYY-MM-DDTHH:mm:ssZ'/ and 'YYYY-MM-DDTHH:mm:ss±hh:mm' " +
            "(with a valid time zone offset).";

    // In Transaction
    String ITEM = "An item included in the transaction";

    String BILLING_ADDRESS = "The billing address for the transaction";

    String SHIPPING_ADDRESS = "The shipping address for the transaction";

    String SALES_TAX = "The sales tax breakdown for the transaction, including the amount and rates by jurisdictions";

    String TRANSACTION_STATUS = "The status of the transaction";

    String TRANSACTION_TYPE = "The type of the transaction. Only invoices are considered for nexus tracking and for actual tax calculation";

    String CREATED_FROM = "The external ID of the document that preceded this transaction creation (e.g., when creating an invoice, you can pass the ID of the sales order that it originated from here)";

    String TAXABLE_ITEMS_AMOUNT = "The amount of taxable items in the transaction";

    String TANGIBLE_ITEMS_AMOUNT = "The amount of tangible items in the transaction";

    String SHIPPING_FEE = "The shipping fee for the transaction";

    // In Customer

    String ADDRESS_OF_CUSTOMER = "The customer's Address";

    String NAME_OF_CUSTOMER = "The customer's name";

    String CUSTOMER_TYPE = "Indicates whether the customer is 'retail'/'reseller'/'marketplace'";

    // In exemption

    String VALIDATION_DATES = "Determains the timeframe that the exemption is valid for";


    String CLASSIFICATION = "Indicates on which product classifications the the customer exepmt for";

}
