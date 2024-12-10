package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.InternalRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.domain.mappers.InternalRatesToCommonRatesMapper;
import com.complyt.repositories.internal_rates.InternalSalesTaxRatesRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class InternalSalesTaxRatesServiceImpl implements SalesTaxRatesService<InternalSalesTaxRates> {

    @NonNull
    InternalSalesTaxRatesRepository internalSalesTaxRatesRepository;

    @NonNull
    ComplytIdHandler<InternalSalesTaxRates> complytIdHandler;

    @NonNull
    TaxRateApplicabilityProcessor taxRateApplicabilityProcessor;

    @Override
    public Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate addressWithDate) {
        return internalSalesTaxRatesRepository.find(addressWithDate)
                .map(internalSalesTaxRates -> {
                    InternalRates applicableRates = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, addressWithDate.getRequiredDate());
                    return InternalRatesToCommonRatesMapper.INSTANCE.map(internalSalesTaxRates.withSalesTaxRates(applicableRates));
                });
    }

    @Override
    public Mono<InternalSalesTaxRates> save(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        internalSalesTaxRates = complytIdHandler.insertComplytIdToNew(
                new InternalSalesTaxRates(null, null, internalSalesTaxRates.getAddress(), internalSalesTaxRates.getSalesTaxRates(),
                        internalSalesTaxRates.getEffectiveDates(), internalSalesTaxRates.getInternalSalesTaxRatesMetaData(), internalSalesTaxRates.getCreatedDate()));

        return internalSalesTaxRatesRepository.save(internalSalesTaxRates);
    }
}