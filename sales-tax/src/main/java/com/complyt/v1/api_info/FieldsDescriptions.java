package com.complyt.v1.api_info;

public interface FieldsDescriptions {

    /**
     * The rules for specifying descriptions for fields in our openAPI implementation are:
     * 1. if it's a single-value field (primitive types, UUID etc.), define description in the fields @Schema annotation directly.
     * 2. if it's a complex object and all objects of same class have same description, define the description in the class @Schema annotation.
     * 3. if it's an object and each instance have a different descriptions, define the description in the OpenApiConfig file
     */

    // Resources
    String customer = "The buyer. All transactions are associated with a customer";

    String transaction = "A transaction between the API consumer and a customer";

    String exemption = "An exemption from sales tax associated with a customer";

    String salesTaxTracking = "Tracks the organization's sales tax status in each US state";

    // General
    String complyt_id = "The unique identifier in Complyt of the "; // add Dto

    String external_id = "the attribute designed to receive a unique identifier provided by API consumers themselves (e.g. pass your own ID)";

    String source = "a single digit which identifies your different stores, checkout solutions, marketplaces, etc. defined by the API consumers (to avoid external id duplications)";

    String customer_id = "The Complyt ID of the customer associated with the "; // add Dto

    String internal_timestamps = "read-only. The APIs internal timestamps for this resource";

    String external_timestamps = "External timestamps, which should reference the dates in your originating system since these are the timestamps used for sales tax calculation";

    // In Transaction
    String item = "An item included in the transaction";

    String billing_address = "The billing address for the transaction";

    String shipping_address = "The shipping address for the transaction";

    String sales_tax = "The sales tax breakdown for the transaction, including the amount and rates by jurisdictions";

    String transaction_status = "The status of the transaction";

    String transaction_type = "The type of the transaction. Only invoices are considered for nexus tracking and for actual tax calculation";

    String created_from = "The external ID of the document that preceded this transaction creation (e.g., when creating an invoice, you can pass the ID of the sales order that it originated from here)";

    String taxable_items_amount = "The amount of taxable items in the transaction";

    String tangible_items_amount = "The amount of tangible items in the transaction";

    String shipping_fee = "The shipping fee for the transaction";

    // In Customer

    String address_of_customer = "The customer's Address";

    String name_of_customer = "The customer's name";

    String customer_type = "Indicates whether the customer is 'retail'/'reseller'/'marketplace'";

    // In exemption

    String validation_dates = "Determains the timeframe that the exemption is valid for";


    String classification = "Indicates on which product classifications the the customer exepmt for";

}
