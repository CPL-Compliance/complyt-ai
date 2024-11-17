package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class StateNotFoundInJurisdictionalTaxRulesApiException extends ComplytApiException {

    public StateNotFoundInJurisdictionalTaxRulesApiException() {
        super(GenericErrorMessages.STATE_NOT_FOUND_IN_JURISDICTIONAL_TAX_RULE);
    }
}