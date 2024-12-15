package com.complyt.v1.handler;

import com.complyt.domain.TaxRates;
import com.complyt.facade.SalesTaxRatesFacade;
import com.complyt.security.permissions.sales_tax_rates.SalesTaxRatesReadPermission;
import com.complyt.utils.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.AddressWithDateMapper;
import com.complyt.v1.mappers.CommonSalesTaxRatesMapper;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.model.common_sales_tax_rates.CommonSalesTaxRatesDto;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComplytSalesTaxRatesHandler {
    @NonNull
    SalesTaxRatesFacade<? extends TaxRates> complytSalesTaxRatesFacade;

    @NonNull
    ValidationHandler<AddressWithDateDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @SalesTaxRatesReadPermission
    public Mono<ServerResponse> getSalesTaxRatesByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<CommonSalesTaxRatesDto> commonSalesTaxRatesDto = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoValidationHandler.validate(serverRequest))
                .map(AddressWithDateMapper.INSTANCE::addressWithDateDtoToAddressDate)
                .flatMap(complytSalesTaxRatesFacade::findByAddress)
                .map(CommonSalesTaxRatesMapper.INSTANCE::commonSalesTaxRatesToCommonSalesTaxRatesDto)
                .flatMap(commonSalesTaxRates -> ContextLogger.observeCtx("<-- Returned Body: " + commonSalesTaxRates, log::info)
                        .thenReturn(commonSalesTaxRates))
                .switchIfEmpty(ContextLogger.observeCtx("Failed to get SalesTaxRates by address", log::error)
                        .then(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(commonSalesTaxRatesDto, CommonSalesTaxRatesDto.class);
    }
}