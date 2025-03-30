package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.complyt.authentication.v1.config.regex.NotNullUndefinedOrBlankRegex;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ParamCheckerFunctions {
    Function<String, Mono<String>>  TENANT_ID_CHECK = ParameterCheckableDefault.createParamCheckerFunction(NotNullUndefinedOrBlankRegex.expression, DtoErrorMessages.TENANT_ID_FORMAT_ERROR);

}
