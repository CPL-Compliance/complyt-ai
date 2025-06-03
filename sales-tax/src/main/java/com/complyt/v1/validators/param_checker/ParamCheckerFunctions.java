package com.complyt.v1.validators.param_checker;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.regex.*;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ParamCheckerFunctions  {
    Function<String, Mono<String>> UUID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(UUID_REGEX.expression, DtoErrorMessages.COMPLYT_ID_FORMAT_ERROR);
    Function<String, Mono<String>> SOURCE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(SourceRegex.expression, DtoErrorMessages.SOURCE_FORMAT_ERROR);
    Function<String, Mono<String>> CUSTOMERID_UUID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(UUID_REGEX.expression, DtoErrorMessages.CUSTOMER_COMPLYT_ID_FORMAT_ERROR);
    Function<String, Mono<String>> PAGE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NumericRegex.pageRegex, DtoErrorMessages.PAGE_FORMAT_ERROR);
    Function<String, Mono<String>> SIZE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NumericRegex.sizeRegex, DtoErrorMessages.SIZE_FORMAT_ERROR);
    Function<String, Mono<String>> EXTERNAL_ID_NOT_NULL_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NotNullUndefinedRegex.expression, DtoErrorMessages.EXTERNAL_ID_NOT_NULL_ERROR);
    Function<String, Mono<String>> STATE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(StateRegex.expression, DtoErrorMessages.STATE_FORMAT_ERROR);
    Function<String, Mono<String>> COUNTRY_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NotNullUndefinedOrBlankRegex.expression, DtoErrorMessages.COUNTRY_FORMAT_ERROR);
    Function<String, Mono<String>> DATE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(ISO8601Regex.expression, "date " + DtoErrorMessages.LOCALDATE_FORMAT_ERROR);
    Function<String, Mono<String>> TENANT_ID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(TenantIdMaxLimitRegex.expression, DtoErrorMessages.TENANT_ID_FORMAT);
    Function<String, Mono<String>> NAME_CHECK = ParameterCheckableDefault.createParamCheckerFunction(MaxLimit256CharactersRegex.expression, "name " + DtoErrorMessages.MAX_256_ERROR);
    Function<String, Mono<String>> SORT_ORDER_CHECK = ParameterCheckableDefault.createParamCheckerFunction(SortOrderRegex.expression, "sort order " + DtoErrorMessages.INVALID_SORT_ORDER_PARAMETER);
    Function<String, Mono<String>> DETAILED_TRANSACTION_OBJECT_CHECK = ParameterCheckableDefault.createParamCheckerFunction(MaxLimit50CharactersRegex.expression, "detailed parameter " + DtoErrorMessages.INVALID_DETAILED_TRANSACTION_PARAMETER);

    // vat validation
    Function<String, Mono<String>> VAT_VALIDATION_COUNTRY_CODE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(MaxLimit50CharactersRegex.expression, "countryCode parameter " + GenericErrorMessages.MIN_1_MAX_50_ERROR);
    Function<String, Mono<String>> VAT_VALIDATION_VAT_NUMBER_CHECK = ParameterCheckableDefault.createParamCheckerFunction(MaxLimit20CharactersRegex.expression, "vatNumber parameter " + GenericErrorMessages.MIN_1_MAX_20_ERROR);
}
