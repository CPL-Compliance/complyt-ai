package com.complyt.v1.config.error_messages;

import com.complyt.v1.api_info.FieldsDescriptions;

public interface DtoErrorMessages {

    String NOT_NULL_ERROR = "may not be null";

    String NOT_NULL_OR_BLANK_ERROR = "may not be null or blank";

    String REGISTERED_CONFLICT = "registered field is false but date was provided";

    String CONFLICTED_WITH_URL_ERROR = "in body and path should be identical";

    String CONFLICTED_WITH_QUERY_PARAM_IN_URL_ERROR = "in query param should be same as the name or abbreviation in body";

    String LIST_NOT_EMPTY_ERROR = "list cannot be empty";

    String COMPLYT_ID_CANNOT_BE_UPDATED_ERROR = "complytId cannot be changed in an update";

    String COMPLYT_ID_IN_A_NEW_RECORD_ERROR = "new record cannot have a complytId field";

    String NON_PARTIAL_ERROR_SUFFIX = "in a non partial address."; // Appended to other errors messages
    String PARTIAL_ERROR_SUFFIX = "in a partial address."; // Appended to other errors messages

    String ISO8601_FORMAT_ERROR = "is in an illegal format - " +
            "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;

    String LOCALDATE_FORMAT_ERROR = "must be in the format yyyy-mm-dd";

    String COMPLYT_ID_FORMAT_ERROR = "complyt ID has to be in UUID format";

    String SOURCE_FORMAT_ERROR = "source has to be in range 1-15";

    String PAGE_FORMAT_ERROR = "Page number must be a non-negative integer.";

    String SIZE_FORMAT_ERROR = "Size must be a positive integer.";

    String EXTERNAL_ID_NOT_NULL_ERROR = "externalId can not be null or undefined";

    String STATE_FORMAT_ERROR = "invalid state provided. Please provide a valid state name or abbreviation.\";";

    String NOT_SUPPORTED_COUNTRY_FORMAT_ERROR = "invalid country provided. Please provide a valid country name or abbreviation.\";";

    String COUNTRY_FORMAT_ERROR = "country must not be null\";";

    String TOTAL_AMOUNT_AFTER_DISCOUNT_IS_BELOW_ZERO = "total transaction price cannot be negative. please create a credit memo/refund with a positive price";

    String ITEM_WITH_NEGATIVE_TOTAL_CANNOT_HAVE_A_DISCOUNT = "item with negative price cannot have a discount";

    String STATE_MUST_NOT_BE_NULL_USA = "in usa addresses state must not be null";

    String STATE_NOT_RECOGNIZED_USA = "in usa address is not recognized.";

    String ZIP_NOT_IN_FORMAT = "format is incorrect. the correct format is mandatory 5 digits and optional dash and and additional 4 digits. example: 12345 or 12345-6789.";

    String STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION = STATE_NOT_RECOGNIZED_USA + " or an invalid combination";

    String ONE_OF_THE_ITEMS_IS_UNALIGNED = "At least one of the item's total price does not have the same sign as the amount (one is negative and the other one is positive)";

    String CURRENCY_IS_NOT_SUPPORTED = "The currency entered is not supported";

    String TENANT_ID_FORMAT = "invalid tenantId provided. please provide a valid parameter";

    String MAX_256_ERROR = "should be up to 256 characters maximum";

    String INVALID_SORT_ORDER_PARAMETER = "should be one of the following asc/desc";

    String INVALID_DETAILED_TRANSACTION_PARAMETER = "should be one of the following true/false";

    String ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE = "All items should have either total price OR quantity and unitPrice";

    String CUSTOMER_MISSING_ID_OR_EXTERNAL_REFERENCE_AND_SOURCE = "ERR-SALES_TAX-001: Either a customerID OR customerExternalReference and customerSource should be provided.";
}