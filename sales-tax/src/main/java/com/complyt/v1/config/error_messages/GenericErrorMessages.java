package com.complyt.v1.config.error_messages;

public interface GenericErrorMessages {

    String DATA_CONFLICT_ERROR = "The requested operation failed because there was an unresolvable conflict between two or more inputs";

    String INTERNAL_SERVER_ERROR = "The request failed due to an internal error. Please contact support@complyt.io if this continues";
    String NOT_FOUND_ERROR = "The requested operation failed because a resource associated with the request could not be found.";
    String CURRENCY_NOT_FOUND_ERROR = "Currency conversion is not supported for the given transaction date.";
    String STATE_NOT_FOUND_IN_JURISDICTIONAL_TAX_RULE = "State was not found in the jurisdictional sales tax rule.";
    String COUNTRY_NOT_FOUND_IN_JURISDICTIONAL_TAX_RULE = "Country was not found in the jurisdictional tax rule.";
    String CUSTOMER_NOT_FOUND = "Customer specified in the object was not found.";
    String ZIP_CODE_NOT_FOUND_ERROR = "The provided ZIP code is not recognized as a valid U.S. ZIP code. Please enter a correct ZIP code.";
    String ZIP_CODE_NOT_VALID_ERROR = "Zip code format is incorrect. For U.S. addresses, please provide a valid 5-digit ZIP code or ZIP+4 format (e.g., 85006 or 85006-5705).";
    String INVALID_PATCH_FIELD_EXCEPTION = "The requested operation failed because of an invalid patch field provided.";
    String INVALID_DATE_TIME_FORMAT_EXCEPTION = "Failed on parsing string to LocalDateTime. LocalDateTime " + DtoErrorMessages.ISO8601_FORMAT_ERROR;

    String MISSING_BODY_ERROR = "The requested operation failed because no request body has been provided";

    String UNSUPPORTED_MEDIA_TYPE = "415 Unsupported Media Type";
    String TENANT_ID_FORMAT = "invalid tenantId provided. please provide a valid parameter";

    String MAX_256_ERROR = "should be up to 256 characters maximum";
    String MIN_1_MAX_50_ERROR = "should be at least 1 character and up to 50 characters maximum";
    String MIN_1_MAX_20_ERROR = "should be at least 1 character and up to 20 characters maximum";

    String NOT_NULL = "may not be null";

    String CONFLICTED_WITH_URL_ERROR = "in body and path should be identical";

    String CONFLICTED_REGISTERED_ERROR = "registered field is false but date was provided";

    String INVALID_DISCOUNT_AMOUNT = "transaction level discount should be positive or zero";

    String INVALID_TAX_CODE = "The tax code entered is not recognized";

    String INVALID_SORT_ORDER_PARAMETER = "sort order should be one of the following asc/desc";
}

