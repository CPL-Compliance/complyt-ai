package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.*;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Map;

@Configuration
public class ValidatorConfig {

    ParameterChecksProvider queryParamChecker = new ParameterChecksProvider(Map.of(
            "tenantId", ParamCheckerFunctions.TENANT_ID_CHECK
    ));

    ShouldCallValidate shouldCallValidate = new ShouldCallValidate(Map.of(
            HttpMethod.POST, "^/v1/token(/.*)?$|"
                    + "^/v1/api_key(/.*)?$|"
                    + "^/v1/api_key/rotate(/.*)?$|"
                    + "^/v1/partnership/client(/.*)?$",
            HttpMethod.DELETE, "^/v1/api_key(/.*)?$",
            HttpMethod.GET, "^/v1/secret_key(/.*)?$"
    ));

    @Bean
    ValidationHandler<CredentialsDto, SpringValidatorAdapter>
    credentialsDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                    @Autowired QueryParamsExtractor<CredentialsDto> queryParamsExtractorEmpty) {
        return new ValidationHandler<>(CredentialsDto.class, springValidatorAdapter,
                queryParamsExtractorEmpty, queryParamChecker, shouldCallValidate);
    }

    @Bean
    public ValidationHandler<ApiKeyDto, SpringValidatorAdapter>
    apiKeyDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                               @Autowired QueryParamsExtractor<ApiKeyDto> queryParamsExtractorCredentials) {
        return new ValidationHandler<>(ApiKeyDto.class, springValidatorAdapter,
                queryParamsExtractorCredentials, queryParamChecker, shouldCallValidate);
    }

    @Bean
    public ValidationHandler<PartnershipDto, SpringValidatorAdapter>
    partnershipValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                 @Autowired QueryParamsExtractor<PartnershipDto> queryParamsExtractorCredentials) {
        return new ValidationHandler<>(PartnershipDto.class, springValidatorAdapter,
                queryParamsExtractorCredentials, queryParamChecker, shouldCallValidate);
    }

    @Bean
    public ValidationHandler<TokenDto, SpringValidatorAdapter>
    tokenValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                 @Autowired QueryParamsExtractor<TokenDto> queryParamsExtractorCredentials) {
        return new ValidationHandler<>(TokenDto.class, springValidatorAdapter,
                queryParamsExtractorCredentials, queryParamChecker, shouldCallValidate);
    }

    @Bean
    public ValidationHandler<ReferralDto, SpringValidatorAdapter>
    referralValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                           @Autowired QueryParamsExtractor<ReferralDto> queryParamsExtractorCredentials) {
        return new ValidationHandler<>(ReferralDto.class, springValidatorAdapter,
                queryParamsExtractorCredentials, queryParamChecker, shouldCallValidate);
    }
}
