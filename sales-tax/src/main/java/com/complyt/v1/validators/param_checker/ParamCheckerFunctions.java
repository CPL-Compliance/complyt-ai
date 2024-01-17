package com.complyt.v1.validators.param_checker;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.*;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ParamCheckerFunctions  {
    Function<String, Mono<String>> UUID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(UUID_REGEX.expression, DtoErrorMessages.COMPLYT_ID_FORMAT_ERROR);
    Function<String, Mono<String>> SOURCE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(SourceRegex.expression, DtoErrorMessages.SOURCE_FORMAT_ERROR);
    Function<String, Mono<String>> NUMERIC_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NumericRegex.expression, DtoErrorMessages.NUMERIC_FORMAT_ERROR);
    Function<String, Mono<String>> NOT_NULL_UNDEFINED_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NotNullUndefinedRegex.expression, DtoErrorMessages.NOT_NULL_UNDEFINED_ERROR);
    Function<String, Mono<String>> STATE_CHECK = ParameterCheckableDefault.createParamCheckerFunction(StateRegex.expression, DtoErrorMessages.STATE_FORMAT_ERROR);

}
