package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.enums.RatesStatus;
import com.complyt.domain.internal_rates.InternalRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.domain.mappers.InternalRatesToCommonRatesMapper;
import com.complyt.repositories.internal_rates.InternalSalesTaxRatesRepository;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
                    InternalRates applicableRates = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, addressWithDate.getEffectiveDate());
                    return InternalRatesToCommonRatesMapper.INSTANCE.map(internalSalesTaxRates.withSalesTaxRates(applicableRates));
                });
    }

    @Override
    public Mono<InternalSalesTaxRates> save(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        return setBeforeSave(internalSalesTaxRates)
                .flatMap(internalSalesTaxRatesRepository::save);
    }

    public Mono<InternalSalesTaxRates> updateRate(@NonNull InternalSalesTaxRates internalRates, @NonNull RatesStatus status) {
        if (status == RatesStatus.NEW) {
            return save(internalRates);
        } else if (status == RatesStatus.ARCHIVE) {
            return archive(internalRates);
        } else if (status == RatesStatus.UPDATE) {
            return findAndUpdate(internalRates);
        } else {
            return Mono.error(new ObjectNotValidApiException(DtoErrorMessages.INVALID_STATUS));
        }
    }

    private Mono<InternalSalesTaxRates> archive(InternalSalesTaxRates internalSalesTaxRates) {
        return internalSalesTaxRatesRepository.archive(internalSalesTaxRates);
    }

    private Mono<InternalSalesTaxRates> findAndUpdate(InternalSalesTaxRates internalSalesTaxRates) {
        return setBeforeSave(internalSalesTaxRates)
                .flatMap(internalSalesTaxRatesRepository::updateRate);
    }

    public Mono<InternalSalesTaxRates> setBeforeSave(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        return Mono.just(complytIdHandler.insertComplytIdToNew(
                new InternalSalesTaxRates(null, null, internalSalesTaxRates.getAddress(), internalSalesTaxRates.getSalesTaxRates(),
                        internalSalesTaxRates.getEffectiveDates(), internalSalesTaxRates.getInternalSalesTaxRatesMetaData(), LocalDateTime.now(), internalSalesTaxRates.getExpiredDate(), internalSalesTaxRates.getAppliedDate(), internalSalesTaxRates.getUpdatedFrom(), internalSalesTaxRates.getUpdatedTo())));
    }
}