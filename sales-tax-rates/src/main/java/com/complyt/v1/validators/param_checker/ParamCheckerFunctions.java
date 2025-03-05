package com.complyt.v1.validators.param_checker;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.StatusRegex;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ParamCheckerFunctions  {
    Function<String, Mono<String>> STATUS_CHECK = ParameterCheckableDefault.createParamCheckerFunction(StatusRegex.expression, DtoErrorMessages.INVALID_STATUS);
}
