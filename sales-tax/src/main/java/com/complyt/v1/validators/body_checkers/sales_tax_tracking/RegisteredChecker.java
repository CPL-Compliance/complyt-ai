package com.complyt.v1.validators.body_checkers.sales_tax_tracking;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.RegisteredTypeDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;

public class RegisteredChecker implements DtoBodyChecker<SalesTaxTrackingDto> {
    @Override
    public Flux<String> check(SalesTaxTrackingDto salesTaxTrackingDto) {
        return (salesTaxTrackingDto.registered() != RegisteredTypeDto.REGISTERED && salesTaxTrackingDto.registrationDate() != null) ?
                Flux.just(DtoErrorMessages.REGISTERED_CONFLICT) : Flux.empty();
    }
}
