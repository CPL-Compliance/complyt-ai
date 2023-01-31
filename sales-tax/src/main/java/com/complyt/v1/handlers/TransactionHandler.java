package com.complyt.v1.handlers;

import com.complyt.domain.Transaction;
import com.complyt.facades.TransactionFacade;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
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
public class TransactionHandler {

    @NonNull
    TransactionFacade transactionFacade;

    @NonNull
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler;

    @TransactionReadPermission
    public Mono<ServerResponse> getByExternalIdAndSource(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        log.debug("Get Transaction by external id and source - id and source received as path variables : " + externalId + ", " + source);
        Mono<TransactionDto> transactionDtoMono = transactionFacade.findByExternalIdAndSource(externalId, source)
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

}
