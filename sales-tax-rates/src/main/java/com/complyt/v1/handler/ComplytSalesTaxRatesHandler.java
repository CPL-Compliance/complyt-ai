package com.complyt.v1.handler;

import com.complyt.facade.ComplytSalesTaxRatesFacade;
import com.complyt.security.permissions.sales_tax_rates.SalesTaxRatesReadPermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.AddressMapper;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ComplytSalesTaxRatesDto;
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
    ComplytSalesTaxRatesFacade complytSalesTaxRatesFacadeFacade;

    @NonNull
    ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @SalesTaxRatesReadPermission
    public Mono<ServerResponse> getSalesTaxRatesByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ComplytSalesTaxRatesDto> complytSalesTaxRatesDto = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoValidationHandler.validate(serverRequest))
                .map(AddressMapper.INSTANCE::addressDtoToAddress)
                .flatMap(complytSalesTaxRatesFacadeFacade::findByAddress)
                .map(ComplytSalesTaxRatesMapper.INSTANCE::complytSalesTaxRatesToComplytSalesTaxRates)
                .flatMap(complytSalesTaxRates -> ContextLogger.observeCtx("<-- Returned Body: " + complytSalesTaxRates, log::info).thenReturn(complytSalesTaxRates))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(complytSalesTaxRatesDto, ComplytSalesTaxRatesDto.class);
    }
}