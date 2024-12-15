package com.complyt.facade;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.TaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface SalesTaxRatesFacade <T extends TaxRates> {
    Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate address);
}