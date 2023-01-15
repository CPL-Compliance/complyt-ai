package com.complyt.v1.validators;

import com.complyt.facades.CustomerFacade;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.models.customer.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CustomerValidationHandler {
    @NonNull
    private final CustomerFacade customerFacade;

    @NonNull
    private final ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler;

    @CustomerUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");

        Mono<CustomerDto> customerDtoMono = customerDtoValidationHandler.validate(serverRequest)
                .map(CustomerMapper.INSTANCE::customerDtoToCustomer).flatMap(receivedCustomer ->
                        customerFacade.findByExternalId(externalId)
                                .flatMap(originalCustomer -> customerFacade.updateIfModified(receivedCustomer, originalCustomer))
                                .switchIfEmpty(customerFacade.saveCustomer(receivedCustomer)))
                .map(CustomerMapper.INSTANCE::customerToCustomerDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }
//
//    protected Mono<ServerResponse> processBody(CustomerDto customerDto, ServerRequest serverRequest) {
//        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
//        Mono<Customer> returnedCustomer = customerFacade.findByExternalId(customerDto.getExternalId())
//                .flatMap(originalCustomer -> customerFacade.updateIfModified(customer, originalCustomer))
//                .switchIfEmpty(customerFacade.saveCustomer(customer));
//
//        Mono<CustomerDto> customerDtoMono = returnedCustomer.map(CustomerMapper.INSTANCE::customerToCustomerDto);
//
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(customerDtoMono, CustomerDto.class);
//    }
}
