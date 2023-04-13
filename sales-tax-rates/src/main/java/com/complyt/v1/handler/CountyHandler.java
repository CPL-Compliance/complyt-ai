package com.complyt.v1.handler;

import com.complyt.facade.CountyFacade;
import com.complyt.security.permissions.county.CountyReadPermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.AddressMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
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
public class CountyHandler {

    @NonNull
    CountyFacade countyFacade;

    @NonNull
    QueryParamsExtractor<AddressDto> addressDtoQueryParamsExtractor;

    @NonNull
    ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @CountyReadPermission
    public Mono<ServerResponse> getCountyByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<String> salesTaxRatesDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoQueryParamsExtractor.extract(serverRequest))
                .flatMap(addressDtoValidationHandler::validate)
                .map(AddressMapper.INSTANCE::addressDtoToAddress)
                .flatMap(countyFacade::findByAddress)
                .flatMap(county -> ContextLogger.observeCtx("<-- Returned Body: " + county, log::info).thenReturn(county))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxRatesDtoMono, String.class);
    }

}