package com.complyt.v1.validators.param_checker;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.*;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ParamCheckerFunctions  {
    Function<String, Mono<String>> UUID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(UUID_REGEX.expression, DtoErrorMessages.COMPLYT_ID_FORMAT_ERROR);
    Function<String, Mono<String>> SOURCE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(SourceRegex.expression, DtoErrorMessages.SOURCE_FORMAT_ERROR);
    Function<String, Mono<String>> PAGE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NumericRegex.expression, DtoErrorMessages.PAGE_FORMAT_ERROR);
    Function<String, Mono<String>> SIZE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NumericRegex.expression, DtoErrorMessages.SIZE_FORMAT_ERROR);
    Function<String, Mono<String>> EXTERNAL_ID_NOT_NULL_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NotNullUndefinedRegex.expression, DtoErrorMessages.EXTERNAL_ID_NOT_NULL_ERROR);
    Function<String, Mono<String>> STATE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(StateRegex.expression, DtoErrorMessages.STATE_FORMAT_ERROR);
    Function<String, Mono<String>> COUNTRY_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NotNullUndefinedOrBlankRegex.expression, DtoErrorMessages.COUNTRY_FORMAT_ERROR);
    Function<String, Mono<String>> DATE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(ISO8601Regex.expression, "date " + DtoErrorMessages.LOCALDATE_FORMAT_ERROR);
    Function<String, Mono<String>> TENANT_ID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(TenantIdMaxLimitRegex.expression, DtoErrorMessages.TENANT_ID_FORMAT);
    Function<String, Mono<String>> NAME_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NameMaxLimitRegex.expression, "name " + DtoErrorMessages.MAX_256_ERROR);
}
